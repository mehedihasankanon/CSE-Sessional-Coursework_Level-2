-- 1. For each country, show country_id, number of cities and number of provinces. Sort 
-- the output in ascending order of country_id. Discard those countries which have no 
-- provinces and less than two cities.

SELECT COUNTRY_ID, COUNT(UNIQUE CITY) AS CITIES, COUNT(UNIQUE STATE_PROVINCE) AS PROVINCES
FROM LOCATIONS
WHERE CITY IS NOT NULL AND STATE_PROVINCE IS NOT NULL
GROUP BY COUNTRY_ID
HAVING COUNT(UNIQUE CITY) >= 2 AND COUNT(UNIQUE STATE_PROVINCE) > 0;



-- 2. Display department IDs in which at most 1 employee had worked for at most 600 days.

SELECT department_id
FROM employees
WHERE (sysdate - hire_date) <= 600
GROUP BY department_id
HAVING COUNT(*) <= 1;

-- 3. Show  the  department_id,  minimum  hire_date,  maximum  hire_date,  and  average 
-- salary of all departments. Sort the list in ascending order of the difference between 
-- maximum  and  minimum  hire_date  of  each  department  and  then  sort  by  average 
-- salary in descending order. Make sure no null values are printed. 

SELECT department_id, MIN(hire_date), MAX(hire_date), AVG(salary)
FROM EMPLOYEES
WHERE hire_date IS NOT NULL AND hire_date IS NOT NULL AND salary IS NOT NULL
GROUP BY department_id
ORDER BY (MAX(hire_date) - MIN(hire_date)) ASC, AVG(salary) DESC;


-- 4. For  each  employee,  display  their  full name  (full  name  includes  first  name,  a  space 
-- and last name), current annual salary, current monthly salary, commission 
-- percentage, and their next year’s increased monthly salary. Consider that employees 
-- will get a year-end increment according to the formula (increment = annual salary * 
-- commission percentage / 100). Employees who do not have any defined commission 
-- percentage will get a 0.05% pay deduction (based on their annual salary) at the end 
-- of the year.

SELECT (first_name || ' ' || last_name) AS name, salary * 12 AS annsal, salary AS monsal, NVL(commission_pct, -0.05) AS commission_pct, salary * (1 + NVL(commission_pct, -0.05) / 100) AS newsal
FROM EMPLOYEES;

-- 5. For each manager, show the manager id, number of employees under his 
-- management  who  joined  in  March,  September  or  December  and  average  salary  of 
-- those  employees.  Show  the  outputs  where  the  average  salary  is  integer.  Print  the 
-- output in the ascending order of number of employees managed and if there is a tie 
-- then  print  those  in  descending  order  of  average  salary.  Make  sure  any  sort  of  null 
-- value is not printed. 

SELECT manager_id, COUNT(*) AS emp_cnt, AVG(salary) AS AVGSAL
FROM EMPLOYEES
WHERE manager_id IS NOT NULL 
  AND hire_date IS NOT NULL 
  AND EXTRACT(MONTH FROM HIRE_DATE) IN (3, 9, 12)
  AND salary IS NOT NULL
GROUP BY manager_id
-- HAVING TRUNC(AVG(salary)) = AVG(salary)
HAVING ROUND(AVG(SALARY), 0) = AVG(SALARY) 
ORDER BY emp_cnt ASC, AVGSAL DESC; 

