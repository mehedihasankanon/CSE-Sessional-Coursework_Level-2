-- 1.   Find   employees   who   are   either   in   departments   with   more   than   5   employees   or  have  a  job  title 
--  with   a   minimum  salary  above  10000,  or  both.  Exclude  those  in  departments  where  the  manager 
--  earns less than their department's average. 

SELECT * FROM EMPLOYEES E
JOIN DEPARTMENTS D ON D.DEPARTMENT_ID = E.DEPARTMENT_ID
JOIN JOBS J ON J.JOB_ID = E.JOB_ID
WHERE (E.DEPARTMENT_ID IN (
    SELECT DEPARTMENT_ID FROM EMPLOYEES
    GROUP BY DEPARTMENT_ID
    HAVING COUNT(*) > 5
) OR J.MIN_SALARY > 10000
) AND E.DEPARTMENT_ID NOT IN (
    SELECT DEPARTMENT_ID
    FROM EMPLOYEES E1 
    GROUP BY E1.DEPARTMENT_ID, E1.MANAGER_ID
    HAVING (
        SELECT E2.SALARY
        FROM EMPLOYEES E2
        WHERE E2.EMPLOYEE_ID = E1.MANAGER_ID
    ) < AVG(E1.SALARY)
);

/

-- OR:

SELECT E.FIRST_NAME, E.LAST_NAME, E.SALARY, D.DEPARTMENT_NAME
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON E.DEPARTMENT_ID = D.DEPARTMENT_ID
JOIN JOBS J ON E.JOB_ID = J.JOB_ID
WHERE (
    -- Condition 1: Dept has more than 5 employees
    E.DEPARTMENT_ID IN (
        SELECT DEPARTMENT_ID 
        FROM EMPLOYEES 
        GROUP BY DEPARTMENT_ID 
        HAVING COUNT(*) > 5
    )
    OR 
    -- Condition 2: Job title has min salary > 10000
    J.MIN_SALARY > 10000
)
AND E.DEPARTMENT_ID NOT IN (
    -- Exclusion: Depts where the Manager earns less than Dept Average
    SELECT d2.DEPARTMENT_ID
    FROM DEPARTMENTS d2
    JOIN EMPLOYEES mgr ON d2.MANAGER_ID = mgr.EMPLOYEE_ID
    JOIN EMPLOYEES emp ON d2.DEPARTMENT_ID = emp.DEPARTMENT_ID
    GROUP BY d2.DEPARTMENT_ID, mgr.SALARY
    HAVING mgr.SALARY < AVG(emp.SALARY)
);
/


--  2.   For   each   country,   count   the   number   of   departments.   Display   only   the   country_name   and 
--  department_count,   in   ascending   order   of   the   country_name.   Include   the   countries   having   no 
--  departments, too. 

SELECT C.COUNTRY_NAME, COUNT(D.DEPARTMENT_ID) AS DEP_CNT 
FROM DEPARTMENTS D
LEFT JOIN LOCATIONS L ON L.LOCATION_ID = D.LOCATION_ID
LEFT JOIN COUNTRIES C ON C.COUNTRY_ID = L.COUNTRY_ID
GROUP BY C.COUNTRY_ID, C.COUNTRY_NAME
ORDER BY C.COUNTRY_NAME ASC;
/

SELECT 
    C.COUNTRY_NAME, 
    COUNT(D.DEPARTMENT_ID) AS DEPARTMENT_COUNT
FROM COUNTRIES C
LEFT JOIN LOCATIONS L ON C.COUNTRY_ID = L.COUNTRY_ID
LEFT JOIN DEPARTMENTS D ON L.LOCATION_ID = D.LOCATION_ID
GROUP BY C.COUNTRY_NAME
ORDER BY C.COUNTRY_NAME ASC;
/




--  3.   For  each  department,  find  the  employee_id,  full_name,  salary,  department_name,  and  job  title  of 
--  the   second-highest-paid   employee(s)   i.e.   employee(s)   having   the   second-highest   salary.   If   a 
--  department  has  fewer  than  two  employees,  do  not  include  it  in  the  results.  Display  the  output  in 
--  descending   order   of   the  salary.  If  two  employees  have  the  same  salary,  prioritize  the  one  whose 
--  department  name  is  lexicographically  smaller.  If  a  tie  still  exists,  prioritize  the  employee  with  the 
--  lower employee_id.  

WITH RANKED_EMPLOYEES AS (
    SELECT 
        E.EMPLOYEE_ID,
        E.FIRST_NAME || ' ' || E.LAST_NAME AS FULL_NAME,
        E.SALARY,
        D.DEPARTMENT_NAME,
        J.JOB_TITLE,
        COUNT(*) OVER (PARTITION BY E.DEPARTMENT_ID) AS DEPT_COUNT,
        DENSE_RANK() OVER (PARTITION BY E.DEPARTMENT_ID ORDER BY E.SALARY DESC) AS SALARY_RANK
    FROM EMPLOYEES E
    JOIN DEPARTMENTS D ON E.DEPARTMENT_ID = D.DEPARTMENT_ID
    JOIN JOBS J ON E.JOB_ID = J.JOB_ID
)
SELECT 
    EMPLOYEE_ID,
    FULL_NAME,
    SALARY,
    DEPARTMENT_NAME,
    JOB_TITLE
FROM RANKED_EMPLOYEES
WHERE SALARY_RANK = 2 AND DEPT_COUNT >= 2
ORDER BY SALARY DESC, DEPARTMENT_NAME ASC, EMPLOYEE_ID ASC;

--  4.   Find  the  employee_id,  first_name,  and  salary  of  employees  in  descending  order  of  the  salary  and 
--  ascending order of the employee ID, who meet  exactly one  of the following two conditions:  a.   They report to a 
-- manager whose salary is greater than 15000.  b.   They work in a department located in 'Seattle'.  

-- EXPLANATION:
-- We need employees meeting EXACTLY ONE condition (XOR logic):
-- Condition A: Manager's salary > 15000
-- Condition B: Department location is Seattle
-- Result: (A AND NOT B) OR (NOT A AND B)

SELECT 
    E.EMPLOYEE_ID,
    E.FIRST_NAME,
    E.SALARY
FROM EMPLOYEES E
JOIN DEPARTMENTS D ON E.DEPARTMENT_ID = D.DEPARTMENT_ID
JOIN LOCATIONS L ON D.LOCATION_ID = L.LOCATION_ID
WHERE (
    -- Case 1: Reports to high-paid manager (salary > 15000) BUT NOT in Seattle
    (E.MANAGER_ID IS NOT NULL 
     AND E.MANAGER_ID IN (
        SELECT EMPLOYEE_ID FROM EMPLOYEES WHERE SALARY > 15000
     ) 
     AND UPPER(L.CITY) != 'SEATTLE')
    OR
    -- Case 2: Works in Seattle BUT does NOT report to high-paid manager
    (UPPER(L.CITY) = 'SEATTLE' 
     AND (E.MANAGER_ID IS NULL 
          OR E.MANAGER_ID NOT IN (
            SELECT EMPLOYEE_ID FROM EMPLOYEES WHERE SALARY > 15000
          )))
)
-- Sort by salary (descending), then by employee_id (ascending) as tiebreaker
ORDER BY E.SALARY DESC, E.EMPLOYEE_ID ASC;
/

--5.   Find  employees  (first  and  last  name),  their  departments,  and  salary,  for  those  who  earn  more  than 
--  the  average  salary  in  their  own  department.  Only  consider  departments  where  there  is  at  least  one 
--  employee   earning   less   than   the   company   average   salary   and  at  least  one  earning  more  than  the 
--  company  average  salary.  Use  a  CASE  statement  to  categorize  salary  as  'High'  (if  above  10,000), 
--  'Medium' (if between 5,000 and 10,000), or 'Low' (if below 5,000).



