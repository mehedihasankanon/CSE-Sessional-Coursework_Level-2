-- 1.  List managers whose departments have average salaries higher than the overall company average, 
-- for departments located in Toronto and Oxford. 

SELECT DISTINCT M.EMPLOYEE_ID, M.FIRST_NAME, M.LAST_NAME 
FROM EMPLOYEES M
WHERE (
    M.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID 
        FROM EMPLOYEES E
        GROUP BY E.DEPARTMENT_ID
        HAVING (AVG(E.SALARY) > (
            SELECT AVG(SALARY)
            FROM EMPLOYEES E
        ))
    )
    AND M.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID 
        FROM DEPARTMENTS D
        JOIN LOCATIONS L 
        ON (L.LOCATION_ID = D.LOCATION_ID)
        WHERE L.CITY IN ('TORONTO', 'OXFORD')
    )
    AND M.EMPLOYEE_ID IN (
        SELECT DISTINCT MANAGER_ID
        FROM EMPLOYEES
        WHERE MANAGER_ID IS NOT NULL
    )
);

 
-- 2.  Find employees who both work in departments with more than 5 employees AND have salaries 
-- greater than the overall average salary across all employees. 

SELECT EMPLOYEE_ID, LAST_NAME, FIRST_NAME 
FROM EMPLOYEES E
WHERE E.DEPARTMENT_ID IN (
    SELECT DEPARTMENT_ID 
    FROM EMPLOYEES 
    GROUP BY DEPARTMENT_ID
    HAVING COUNT(*) > 5
) AND SALARY > (
    SELECT AVG(SALARY)
    FROM EMPLOYEES
);

-- 3.  Write a SQL query for employees in departments that have managers, with no job history records, 
-- and salary > dept average. Show full_name, salary, dept_name, and label 'Stable High Earner' if 
-- salary > 1.7 times dept average, else 'Dept Above Avg'. 

SELECT E.FIRST_NAME || ' ' || E.LAST_NAME AS FULL_NAME, E.SALARY, D.DEPARTMENT_NAME, 
    (
        CASE 
            WHEN E.SALARY > 1.7 * (SELECT AVG(SALARY) FROM EMPLOYEES WHERE DEPARTMENT_ID = E.DEPARTMENT_ID)
            THEN 'STABLE HIGH EARNER'
            ELSE 'DEPT ABOVE AVG'
        END
    ) AS LABEL
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
WHERE D.DEPARTMENT_ID IN (
    SELECT DISTINCT DEPARTMENT_ID 
    FROM EMPLOYEES 
    WHERE MANAGER_ID IS NOT NULL
)
AND E.EMPLOYEE_ID NOT IN (
    SELECT EMPLOYEE_ID 
    FROM JOB_HISTORY
)
AND E.SALARY > (
    SELECT AVG(SALARY) 
    FROM EMPLOYEES 
    WHERE DEPARTMENT_ID = E.DEPARTMENT_ID
);


-- 4.  Find employees who are either in departments with more than 5 employees or have a job with 
-- minimum salary above 10000. 
-- Display: employee_id, first_name, last_name, department_id, job_id, salary. 

SELECT DISTINCT EMPLOYEE_ID, E.FIRST_NAME, E.LAST_NAME, E.DEPARTMENT_ID,E.JOB_ID, E.SALARY 
FROM EMPLOYEES E 
WHERE E.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID 
        FROM EMPLOYEES
        GROUP BY DEPARTMENT_ID
        HAVING COUNT(*) > 5
    )
OR E.JOB_ID IN (
    SELECT JOB_ID
    FROM JOBS
    WHERE MIN_SALARY > 10000
);


-- 5.  Write an SQL query to find employees who satisfy exactly one of the following conditions:  
-- (i) they work in a department with more than 5 employees, or  (ii) their job has a minimum salary greater than 10000. 
-- Employees who satisfy both conditions or neither condition must be excluded. Display employee 
-- ID, full name, department ID, job ID, and salary.

SELECT DISTINCT EMPLOYEE_ID, E.FIRST_NAME || ' ' || E.LAST_NAME AS FULL_NAME, E.DEPARTMENT_ID,E.JOB_ID, E.SALARY 
FROM EMPLOYEES E 
WHERE (E.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID 
        FROM EMPLOYEES
        GROUP BY DEPARTMENT_ID
        HAVING COUNT(*) > 5
    )
AND E.JOB_ID NOT IN (
    SELECT JOB_ID
    FROM JOBS
    WHERE MIN_SALARY > 10000
)) OR (E.DEPARTMENT_ID NOT IN (
        SELECT DEPARTMENT_ID 
        FROM EMPLOYEES
        GROUP BY DEPARTMENT_ID
        HAVING COUNT(*) > 5
    )
AND E.JOB_ID IN (
    SELECT JOB_ID
    FROM JOBS
    WHERE MIN_SALARY > 10000
));
