-- 1. Find  the  employee id,  full name  (first  name  <space>  last name),  department id, 
-- total  monthly  salary  with  commission  percentage  of  the  employees  whose  first 
-- name starts with ‘D’ and the fourth character of the last name is ‘n’ and works in 
-- departments with ID between 20 and 70. (Assume zero commission percentage 
-- for the employees with null commission percentage)

SELECT EMPLOYEE_ID, (FIRST_NAME || ' ' || LAST_NAME) AS NAME, department_id, SALARY, NVL(commission_pct, 0)
FROM EMPLOYEES
WHERE UPPER(SUBSTR(FIRST_NAME, 1,1 )) = 'D' AND UPPER(SUBSTR(LAST_NAME, 4,1)) = 'N' AND department_id >= 20 AND department_id <= 70;


-- 2. Display the job IDs of jobs that were held by at least two employees for a duration 
-- of at most 1500 days.

SELECT JOB_ID 
FROM EMPLOYEES
WHERE (SYSDATE - HIRE_DATE) <= 1500
GROUP BY JOB_ID
HAVING COUNT(*) >= 2;

-- 3. Show the department_id, minimum, maximum, and average salary of all 
-- departments  except  depeartment_id 50.  Sort the  list  in  descending  order  of the 
-- difference  between  maximum  salary  and  minimum  salary  of  each  department 
-- and then  sort  by  average  salary  in ascending  order.  (The  average  salary value  is 
-- rounded to four decimal digits)

SELECT DEPARTMENT_ID, MIN(SALARY), MAX(SALARY), ROUND(AVG(SALARY), 4)
FROM EMPLOYEES
WHERE DEPARTMENT_ID <> 50
GROUP BY DEPARTMENT_ID
ORDER BY (MAX(SALARY) - MIN(SALARY)) DESC, ROUND(AVG(SALARY), 4) ASC;


-- 4. For each manager who manages less than 10 employees, show the manager id, 
-- number  of  employees  and  the  average  salary  of  the  employees  managed  by 
-- him/her. Show the outputs with an average salary less than 4000. Print the output 
-- in the ascending order of number of employees managed and if there is a tie then 
-- print those in descending order of average salary. Make sure any sort of null value 
-- is not printed.

SELECT MANAGER_ID, COUNT(*) AS "NUM_EMPLOYEES", AVG(SALARY) AS "AVG_SAL"
FROM EMPLOYEES
WHERE MANAGER_ID IS NOT NULL
GROUP BY MANAGER_ID
HAVING COUNT(*) < 10 AND AVG(SALARY) < 4000
ORDER BY COUNT(*) ASC, AVG(SALARY) DESC;



-- 5. Find the last name of those employees whose last name contains odd number of 
-- consonants. 
 
SELECT LAST_NAME FROM EMPLOYEES
WHERE MOD((LENGTH(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(UPPER(LAST_NAME),'A', ''), 'E', ''), 'I', ''), 'O', ''), 'U', ''))), 2) = 1;