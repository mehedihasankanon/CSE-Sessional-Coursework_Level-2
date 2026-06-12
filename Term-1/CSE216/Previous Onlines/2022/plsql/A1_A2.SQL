-- Write a PL/SQL procedure named LONGEST_SERVING_EMPLOYEE that takes a 
-- REGION_NAME as input and identifies the employee who has been working for the longest time within 
-- that region. 
-- Retrieve and display the following details for that employee. 
-- • Full Name (First Name + Last Name) 
-- • Job Title 
-- • Hire Date 
-- • Country Name 
-- • Department Name 
-- • City 
-- Make sure to handle exceptions with appropriate messages.

CREATE OR REPLACE PROCEDURE LONGEST_SERVING_EMPLOYEE(REGION_NAME VARCHAR2) IS
    V_EID NUMBER;
    V_WORKING_TIME NUMBER := 0;

    V_NAME VARCHAR2(100);
    V_JOB_TITLE VARCHAR2(100);
    V_HIRE_DATE DATE;
    V_COUNTRY_NAME VARCHAR2(100);
    V_CITY VARCHAR2(100);
    V_DEPT_NAME VARCHAR2(100);

    CURSOR CR IS 
        SELECT EMPLOYEE_ID, HIRE_DATE
        FROM EMPLOYEES E
        JOIN DEPARTMENTS D  ON E.DEPARTMENT_ID  = D.DEPARTMENT_ID
        JOIN LOCATIONS L    ON L.LOCATION_ID     = D.LOCATION_ID
        JOIN COUNTRIES C    ON C.COUNTRY_ID      = L.COUNTRY_ID
        JOIN REGIONS R      ON R.REGION_ID       = C.REGION_ID
        WHERE R.REGION_NAME = LONGEST_SERVING_EMPLOYEE.REGION_NAME;  -- join to REGIONS, not LOCATIONS
BEGIN

    FOR IT IN CR LOOP
        -- Fix: update V_WORKING_TIME when a longer-serving employee is found
        IF V_WORKING_TIME < SYSDATE - IT.HIRE_DATE THEN
            V_WORKING_TIME := SYSDATE - IT.HIRE_DATE;
            V_EID := IT.EMPLOYEE_ID;
        END IF;
    
    END LOOP;

    SELECT 
        E.FIRST_NAME || ' ' || E.LAST_NAME,  -- fix: space between names
        J.JOB_TITLE,
        E.HIRE_DATE,
        C.COUNTRY_NAME,                       -- fix: join COUNTRIES to get name
        D.DEPARTMENT_NAME,
        L.CITY
    INTO
        V_NAME,
        V_JOB_TITLE,
        V_HIRE_DATE,
        V_COUNTRY_NAME,
        V_DEPT_NAME,
        V_CITY
    FROM EMPLOYEES E 
    JOIN JOBS J         ON E.JOB_ID          = J.JOB_ID
    JOIN DEPARTMENTS D  ON D.DEPARTMENT_ID   = E.DEPARTMENT_ID
    JOIN LOCATIONS L    ON L.LOCATION_ID     = D.LOCATION_ID
    JOIN COUNTRIES C    ON C.COUNTRY_ID      = L.COUNTRY_ID
    WHERE E.EMPLOYEE_ID = V_EID;

    DBMS_OUTPUT.PUT_LINE(V_NAME 
        || ', ' || V_JOB_TITLE 
        || ', ' || V_HIRE_DATE 
        || ', ' || V_COUNTRY_NAME 
        || ', ' || V_DEPT_NAME
        || ', ' || V_CITY);

EXCEPTION 
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('NOTHING FOUND');
    WHEN OTHERS THEN 
        DBMS_OUTPUT.PUT_LINE('UNKNOWN ERROR: ' || SQLERRM);
END LONGEST_SERVING_EMPLOYEE;
/

EXEC LONGEST_SERVING_EMPLOYEE('Americas');
/

-- Write a procedure named RANK_JOBS in Oracle HR schema. The procedure should rank jobs based on 
-- the following criteria: 
-- 1. Number of Employees: Jobs should be ranked in descending order of their number of 
-- employees. The job with the highest number of employees should be ranked as 1. 
-- 2. Average Salary: If multiple jobs have the same number of employees, the ranking should be 
-- determined based on the average salary in that job, in descending order. 
-- The output should display the following information: 
-- o Rank 
-- o Job Title 
-- o Total Number of Employees 
-- o Average Salary 
-- o Maximum Salary 
-- o Minimum Salary

CREATE OR REPLACE PROCEDURE RANK_JOBS IS

    V_RANK NUMBER := 0;

    CURSOR CR IS
        SELECT 
            COUNT(*) AS EMP_CNT,
            ROUND(AVG(E.SALARY), 2) AS AVG_SAL,
            J.JOB_TITLE,
            J.MAX_SALARY,
            J.MIN_SALARY
        FROM EMPLOYEES E
        JOIN JOBS J ON J.JOB_ID = E.JOB_ID
        -- BASICALLY JA SELECT KORBI SHOB DIYA GROUP KORBI
        GROUP BY J.JOB_ID, J.JOB_TITLE, J.MAX_SALARY, J.MIN_SALARY
        ORDER BY COUNT(*) DESC, AVG(E.SALARY) DESC;

BEGIN
    
    FOR IT IN CR LOOP
        V_RANK := V_RANK + 1;

        DBMS_OUTPUT.PUT_LINE('RANK ' || V_RANK);
        DBMS_OUTPUT.PUT_LINE('-----------');        
        DBMS_OUTPUT.PUT_LINE('JOB TITLE: ' || IT.JOB_TITLE );
        DBMS_OUTPUT.PUT_LINE('EMPLOYEES: ' || IT.EMP_CNT);
        DBMS_OUTPUT.PUT_LINE('AVG SALARY: ' || IT.AVG_SAL);
        DBMS_OUTPUT.PUT_LINE('MAX SALARY: ' || IT.MAX_SALARY );
        DBMS_OUTPUT.PUT_LINE('MIN SALARY: ' || IT.MIN_SALARY );
        
    END LOOP;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('FAAAHH');

END RANK_JOBS;
/

EXEC RANK_JOBS;
/



-- Create a trigger that activates when an employee leaves the job (i.e., when a DELETE operation is 
-- performed on the Employee table). 
 
-- 1 new table: Leaves 
-- (fields: 
-- employee_id,  
-- employee_working_instead_of_him/her,  
-- current date 
-- ) 
 
-- Conditions: 
-- 1. If that employee has a manager, his/her work should be done by the employee who has the same 
-- manager and has the closest salary to him\her. 
-- 2. In  case  the  employee  is  a  manager,  his/her  work  is  done  by  a  manager  who  has  the  closest 
-- subordinate count as him/her.  
-- 3. If an employee meets both conditions 1 and 2 (is a manager and has a manager), go for 2.  
-- 4. If no substitute employee is found, keep that field null. 
-- No changes in the Job table and the Job_history table are necessary for your ease.

CREATE TABLE LEAVES 
(
    EMPLOYEE_ID NUMBER REFERENCES EMPLOYEES(EMPLOYEE_ID),
    EMPLOYEE_WORKING_INSTEAD NUMBER REFERENCES EMPLOYEES(EMPLOYEE_ID),  -- fix: missing NUMBER type
    LEAVE_DATE DATE DEFAULT SYSDATE                                      -- fix: CURRENT_DATE is a reserved keyword
);
/

CREATE OR REPLACE TRIGGER LEAVE
BEFORE DELETE 
ON employees_copy
FOR EACH ROW
DECLARE 

    V_EID NUMBER := :OLD.EMPLOYEE_ID;
    V_MANAGER_ID NUMBER;
    V_SUB_ID NUMBER := NULL;
    V_SUB_CNT NUMBER;
    V_SALARY NUMBER;
    V_JOB_ID VARCHAR2(10);  -- fix: JOB_ID is VARCHAR2(10), not NUMBER

BEGIN 

    SELECT COUNT(*) INTO V_SUB_CNT
    FROM EMPLOYEES_COPY
    WHERE MANAGER_ID = V_EID;

    SELECT MANAGER_ID, SALARY, JOB_ID INTO V_MANAGER_ID, V_SALARY, V_JOB_ID FROM EMPLOYEES WHERE EMPLOYEE_ID = V_EID;
    
    -- Condition 2 takes priority (spec rule 3): check if employee IS a manager first
    IF V_SUB_CNT > 0 THEN  -- fix: already have V_SUB_CNT, no need for SELECT in IF
        -- Find manager whose subordinate count is closest to V_SUB_CNT
        SELECT EMPLOYEE_ID INTO V_SUB_ID
        FROM (
            SELECT M.EMPLOYEE_ID
            FROM EMPLOYEES_COPY M
            JOIN EMPLOYEES_COPY S ON S.MANAGER_ID = M.EMPLOYEE_ID
            WHERE M.EMPLOYEE_ID != V_EID
            GROUP BY M.EMPLOYEE_ID
            ORDER BY ABS(COUNT(S.EMPLOYEE_ID) - V_SUB_CNT) ASC
        ) WHERE ROWNUM = 1;  -- fix: was selecting MANAGER_ID and missing ROWNUM

    ELSIF V_MANAGER_ID IS NOT NULL THEN  -- fix: ELSE IF → ELSIF (ELSE IF needs its own END IF)
        -- Find peer with closest salary (same manager, not self)
        SELECT EMPLOYEE_ID INTO V_SUB_ID
        FROM (
            SELECT EMPLOYEE_ID 
            FROM EMPLOYEES_COPY
            WHERE MANAGER_ID = V_MANAGER_ID  -- fix: must be same manager (peer)
              AND EMPLOYEE_ID != V_EID
            ORDER BY ABS(SALARY - V_SALARY) ASC
        ) WHERE ROWNUM = 1;
    
    END IF;

    IF V_SUB_ID IS NOT NULL THEN
        UPDATE EMPLOYEES_COPY
        SET JOB_ID = V_JOB_ID
        WHERE EMPLOYEE_ID = V_SUB_ID;
    END IF;
    
    INSERT INTO LEAVES (EMPLOYEE_ID, EMPLOYEE_WORKING_INSTEAD) 
    VALUES (V_EID, V_SUB_ID);

EXCEPTION
    WHEN OTHERS THEN 
        DBMS_OUTPUT.PUT_LINE('??');

END LEAVE;
/

