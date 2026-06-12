-- 1. Count the number of employees hired during each quarter of the year. [Hint: You 
-- can  convert  a  number  (e.g.,  1)  into  its  corresponding month ‘Jan’ by using the 
-- function to_char (to_date (1,‘MM’), ‘Mon’)]

SELECT COUNT(*) 
FROM employees
GROUP BY CEIL((EXTRACT(MONTH FROM HIRE_DATE))/3);

-- 2. Display  the  full  name  (full name includes  first  name,  a  space  and  last name) of 
-- the employees formatted as right justified in the column, in such a way that a total 
-- 20 characters are shown including the name. Order the result in ascending order 
-- of the length of their full names. 
 
SELECT LPAD(FIRST_NAME || ' ' || LAST_NAME, 20)
FROM EMPLOYEES
ORDER BY LENGTH(FIRST_NAME || ' ' || LAST_NAME);

-- 3. Display the country id and address for each of the locations. Address should be 
-- generated in the format (street_address, city, state_province - postal_code). Your 
-- address should be displayed only when all the required fields are available. Order 
-- the result in lexicographic order of the country id. In case of any tie, break it by the 
-- reverse lexicographic order of the postal code. 

SELECT
    COUNTRY_ID,
    (STREET_ADDRESS || ', ' || CITY || ', ' || STATE_PROVINCE || ' - ' || POSTAL_CODE) AS "ADDR"
FROM
    LOCATIONS
WHERE
    COUNTRY_ID IS NOT NULL
    AND STREET_ADDRESS IS NOT NULL
    AND CITY IS NOT NULL
    AND STATE_PROVINCE IS NOT NULL
    AND POSTAL_CODE IS NOT NULL
ORDER BY
    COUNTRY_ID ASC,
    POSTAL_CODE DESC;
 
-- 4. For each department and each job, show the department id, job id, the first hiring 
-- date,  the  last  hiring  date  and  average  salary.  Show  the  outputs  with  an  average 
-- salary  more  than  8000.  Make  sure  any  sort  of  null  value  is  not  printed  and  the 
-- average salary value is rounded to two decimal digits. Order the result by 
-- department id. 

SELECT
    DEPARTMENT_ID,
    JOB_ID,
    MIN(HIRE_DATE) AS FIRST_HIRE,
    MAX(HIRE_DATE) AS LAST_HIRE,
    ROUND(AVG(SALARY), 2) AS AVGSAL
FROM 
    EMPLOYEES
WHERE 
    DEPARTMENT_ID IS NOT NULL
    AND JOB_ID IS NOT NULL
GROUP BY 
    DEPARTMENT_ID,
    JOB_ID
HAVING
    ROUND(AVG(SALARY), 2) > 8000
;

 
-- 5. Show the full  name  (full name includes  first name,  a  space  and last  name)  and 
-- hire date of all employees whose first name starts with a consonant and the last 
-- name  does  not  contain  the  letter  B/b,  and  who  have  joined  in  November.  Show 
-- the hire date along with the full name of the employees. Show the results in the 
-- lexicographical order of full name.

SELECT 
    (FIRST_NAME || ' ' || LAST_NAME) AS "FULLNAME",
    HIRE_DATE
FROM 
    EMPLOYEES
WHERE 
    UPPER(SUBSTR(FIRST_NAME, 1,1)) NOT IN ('A', 'E', 'I', 'O', 'U')
    AND LENGTH(REPLACE(UPPER(LAST_NAME), 'B', '')) = LENGTH(UPPER(LAST_NAME))
    AND EXTRACT(MONTH FROM HIRE_DATE) = 11
ORDER BY
    "FULLNAME" ASC
;
