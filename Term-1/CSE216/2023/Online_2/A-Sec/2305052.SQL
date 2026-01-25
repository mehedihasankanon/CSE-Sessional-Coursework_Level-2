-- 1. Find job titles in departments where employees have worked more than 5 years on average, but
-- only include those jobs which have a maximum salary higher than the average max salary across
-- all jobs.
-- CORRECTION: GROUP BY was over-specified; SYSDATE arithmetic needs TRUNC. Changed to proper grouping.

SELECT DISTINCT J.JOB_TITLE
FROM EMPLOYEES E
LEFT JOIN DEPARTMENTS D ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
LEFT JOIN JOBS J ON (E.JOB_ID = J.JOB_ID)
WHERE J.MAX_SALARY > (SELECT AVG(MAX_SALARY) FROM JOBS)
GROUP BY D.DEPARTMENT_ID, J.JOB_ID, J.JOB_TITLE
HAVING AVG(TRUNC(SYSDATE) - TRUNC(E.HIRE_DATE)) > 5 * 365;

DESCRIBE JOBS;


-- 2. Find employees who earn more than their department's average salary but do NOT work in
-- departments with more than 5 employees.

SELECT *
FROM EMPLOYEES E
WHERE (E.DEPARTMENT_ID IN (
    SELECT E1.DEPARTMENT_ID
    FROM EMPLOYEES E1
    GROUP BY E1.DEPARTMENT_ID
    HAVING COUNT(*) <= 5
)) AND (E.SALARY > (
    SELECT AVG(E2.SALARY)
    FROM EMPLOYEES E2
    WHERE E.DEPARTMENT_ID = E2.DEPARTMENT_ID
    GROUP BY E2.DEPARTMENT_ID
));

-- 3. Write a SQL query to find employees from the USA who have a manager (using EXISTS), no
-- job_history records (using NOT EXISTS), and salary greater than their department average.
-- Display full_name, salary, and use a CASE statement to label 'USA Star' if salary > 1.4 times
-- department average, otherwise 'USA Above'.
-- CORRECTION: EXISTS/NOT EXISTS were NOT CORRELATED with outer query - always returned constant result.
-- Fixed: Join to LOCATIONS to filter USA, check MANAGER_ID IS NOT NULL, and correlate NOT EXISTS to employee.

SELECT 
    E.FIRST_NAME || ' ' || E.LAST_NAME AS FULL_NAME,
    E.SALARY,
    (
        CASE
            WHEN E.SALARY > 1.4 * (
                SELECT AVG(SALARY)
                FROM EMPLOYEES E3
                WHERE E3.DEPARTMENT_ID = E.DEPARTMENT_ID
            )
            THEN 'USA STAR'
            ELSE 'USA ABOVE'
        END
    ) AS LABEL
FROM EMPLOYEES E
LEFT JOIN DEPARTMENTS D ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
LEFT JOIN LOCATIONS L ON (D.LOCATION_ID = L.LOCATION_ID)
WHERE L.COUNTRY_ID = 'USA'
  AND E.MANAGER_ID IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 
    FROM JOB_HISTORY JH
    WHERE JH.EMPLOYEE_ID = E.EMPLOYEE_ID
  ) 
  AND E.SALARY > (
    SELECT AVG(SALARY)
    FROM EMPLOYEES E3
    WHERE E3.DEPARTMENT_ID = E.DEPARTMENT_ID
  );

-- DESCRIBE EMPLOYEES; DESCRIBE DEPARTMENTS; DESCRIBE LOCATIONS;


-- 4. Write an SQL query to list all departments where every employee earns more than 5000. For each
-- department, display the department name, the number of employees in that department, and a
-- column called Salary_Level that uses a CASE statement to show 'Above' if the department’s
-- average salary is higher than the overall company average salary, or 'Below or Equal' if it is not.

SELECT D.DEPARTMENT_NAME, COUNT(*) AS EMP_CNT, (
        CASE
            WHEN AVG(E.SALARY) > (SELECT AVG(SALARY) FROM EMPLOYEES)
                THEN 'ABOVE'
            ELSE 
                'BELOW OR EQUAL'
        END
    ) AS SALARY_LEVEL
FROM EMPLOYEES E
LEFT JOIN DEPARTMENTS D ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
GROUP BY E.DEPARTMENT_ID, D.DEPARTMENT_ID, D.DEPARTMENT_NAME
HAVING MIN(E.SALARY) > 5000;

DESCRIBE DEPARTMENTS;

-- 5. Write an SQL query to find employees who earn more than the highest salary of at least one other
-- department. Only include employees whose department has at least 3 employees and who do not
-- have any records in the JOB_HISTORY table. For each qualifying employee, display the
-- employee ID, full name, department name, and salary.
-- CORRECTION: Query was incomplete - missing FULL_NAME and DEPARTMENT_NAME columns, missing department
-- size check (>= 3), and missing JOB_HISTORY check. Subquery syntax was invalid (MIN of GROUP BY aggregate).

SELECT E.EMPLOYEE_ID, 
       E.FIRST_NAME || ' ' || E.LAST_NAME AS FULL_NAME,
       D.DEPARTMENT_NAME,
       E.SALARY
FROM EMPLOYEES E
LEFT JOIN DEPARTMENTS D ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
WHERE E.SALARY > (
        SELECT MIN(MAX_SALARY)
        FROM (
            SELECT DEPARTMENT_ID, MAX(SALARY) AS MAX_SALARY
            FROM EMPLOYEES
            WHERE DEPARTMENT_ID != E.DEPARTMENT_ID
            GROUP BY DEPARTMENT_ID
        )
    )
  AND E.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID
        FROM EMPLOYEES
        GROUP BY DEPARTMENT_ID
        HAVING COUNT(*) >= 3
    )
  AND NOT EXISTS (
        SELECT 1
        FROM JOB_HISTORY JH
        WHERE JH.EMPLOYEE_ID = E.EMPLOYEE_ID
    );