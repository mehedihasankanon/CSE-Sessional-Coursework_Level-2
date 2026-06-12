-- 1.  Find employees earning above their department's average salary in departments with more than 4 
-- employees. 

SELECT E.EMPLOYEE_ID, 
       E.FIRST_NAME, 
       E.LAST_NAME 
FROM EMPLOYEES E
JOIN DEPARTMENTS D 
    ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
WHERE E.DEPARTMENT_ID IN (
    SELECT DEPARTMENT_ID 
    FROM EMPLOYEES 
    GROUP BY DEPARTMENT_ID 
    HAVING COUNT(*) > 4
)
AND E.SALARY > (
    SELECT AVG(SALARY) 
    FROM EMPLOYEES 
    WHERE DEPARTMENT_ID = E.DEPARTMENT_ID
);


-- 2.  Find employees who either earn more than their manager's salary or have a salary greater than 
-- their department's average salary. Print employee details with the type as either "Higher Than 
-- Manager" or "Above Dept Avg". 

SELECT E.EMPLOYEE_ID, 
       E.FIRST_NAME, 
       E.LAST_NAME, 
       CASE
           WHEN E.SALARY > (
               SELECT M.SALARY
               FROM EMPLOYEES M
               WHERE E.MANAGER_ID = M.EMPLOYEE_ID 
           ) THEN 'HIGHER THAN MANAGER'
           WHEN E.SALARY > (
               SELECT AVG(SALARY)
               FROM EMPLOYEES E1 
               WHERE E1.DEPARTMENT_ID = E.DEPARTMENT_ID
           ) THEN 'ABOVE DEPT AVG'
       END AS TYPE_LABEL
FROM EMPLOYEES E
WHERE E.SALARY > (
    SELECT M.SALARY
    FROM EMPLOYEES M
    WHERE E.MANAGER_ID = M.EMPLOYEE_ID 
)
OR E.SALARY > (
    SELECT AVG(SALARY)
    FROM EMPLOYEES E1 
    WHERE E1.DEPARTMENT_ID = E.DEPARTMENT_ID
);
 
-- 3.  Write a SQL query for employees whose salary beats their department average and whose 
-- manager's salary beats the company average. Show full_name, salary, department_name, and 
-- label it 'Dept Top Earner' if salary > 1.5 times dept average, else 'Dept Above Avg'. 

SELECT E.FIRST_NAME || ' ' || E.LAST_NAME AS full_name, 
       E.SALARY, 
       D.DEPARTMENT_NAME,
       CASE
           WHEN E.SALARY > (1.5 * (
               SELECT AVG(SALARY)
               FROM EMPLOYEES
               WHERE DEPARTMENT_ID = E.DEPARTMENT_ID
           )) THEN 'DEPT TOP EARNER'
           ELSE 'DEPT ABOVE AVG'
       END AS salary_label
FROM EMPLOYEES E
JOIN DEPARTMENTS D 
    ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
JOIN EMPLOYEES M 
    ON (E.MANAGER_ID = M.EMPLOYEE_ID)
WHERE E.SALARY > (
    SELECT AVG(SALARY)
    FROM EMPLOYEES
    WHERE DEPARTMENT_ID = E.DEPARTMENT_ID
)
AND M.SALARY > (
    SELECT AVG(SALARY) 
    FROM EMPLOYEES
);

-- 4.  Find employee_id, full name, and department name of employees whose department is located in 
-- the same city as their manager's department.

SELECT E.EMPLOYEE_ID, 
       E.FIRST_NAME || ' ' || E.LAST_NAME AS full_name, 
       D.DEPARTMENT_NAME
FROM EMPLOYEES E
JOIN DEPARTMENTS D 
    ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
JOIN EMPLOYEES M 
    ON (E.MANAGER_ID = M.EMPLOYEE_ID)
JOIN DEPARTMENTS MD 
    ON (M.DEPARTMENT_ID = MD.DEPARTMENT_ID)
WHERE D.LOCATION_ID = MD.LOCATION_ID;


 
-- 5.  Write an SQL query to list all departments that satisfy the following conditions:  (i) every employee in the department earns more than 5000,  
-- (ii) the department has at least one employee with job history, and  
-- (iii) the maximum salary in the department is greater than the overall company average salary. 
-- For each such department, display the department name, number of employees, average salary, and a 
-- column called Salary_Level that shows 
-- ●  'Elite' if the department’s average salary is greater than 1.5 times the company average salary, 
-- ●  'Above Average' otherwise. 
 
 SELECT E.DEPARTMENT_ID, 
       D.DEPARTMENT_NAME, 
       COUNT(*) AS EMPLOYEE_CNT, 
       AVG(E.SALARY) AS AVSAL, 
       CASE 
           WHEN AVG(E.SALARY) > (1.5 * (
               SELECT AVG(SALARY) 
               FROM EMPLOYEES
           ))
           THEN 'ELITE'
           ELSE 'ABOVE AVERAGE' 
       END AS Salary_Level
FROM EMPLOYEES E
JOIN DEPARTMENTS D 
ON (E.DEPARTMENT_ID = D.DEPARTMENT_ID)
LEFT JOIN JOB_HISTORY JH ON (E.EMPLOYEE_ID = JH.EMPLOYEE_ID)
GROUP BY E.DEPARTMENT_ID, D.DEPARTMENT_NAME
HAVING MIN(E.SALARY) > 5000
    AND COUNT(DISTINCT JH.EMPLOYEE_ID) > 0
    AND MAX(E.SALARY) > (SELECT AVG(SALARY) FROM EMPLOYEES);