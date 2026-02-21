-- Write a PL/SQL function named IS_READY_FOR_PROMOTION that takes an EMPLOYEE_ID as 
-- input and returns a VARCHAR2 result indicating whether the employee is ready for promotion. 
-- The function should return: 
-- • YES if the employee meets all the following criteria: 
-- o Has worked for at least 5 years since the HIRE_DATE 
-- o Has a SALARY greater than the midpoint of their job’s MIN_SALARY and 
-- MAX_SALARY 
-- o Manages at least one subordinate. 
-- • NO if the employee exists but fails any of the above conditions 
-- For demonstration, you need to call the function IS_READY_FOR_PROMOTION for each employee 
-- and output whether they are eligible or not. Make sure to handle exceptions with appropriate messages. 

CREATE OR REPLACE FUNCTION IS_READY_FOR_PROMOTION(EID NUMBER) RETURN VARCHAR2 IS
-- DECLARE
    CRIT1 BOOLEAN;
    CRIT2 BOOLEAN;
    CRIT3 BOOLEAN;
    HDATE DATE;
    ESAL NUMBER;
    JOB_MAX_SAL NUMBER;
    JOB_MIN_SAL NUMBER;
    JID VARCHAR2(10);
    CNT NUMBER;
BEGIN

    SELECT HIRE_DATE, SALARY, JOB_ID INTO HDATE, ESAL, JID
    FROM EMPLOYEES e
    WHERE E.EMPLOYEE_ID = EID;

    SELECT MIN_SALARY, MAX_SALARY INTO JOB_MIN_SAL, JOB_MAX_SAL
    FROM JOBS
    WHERE JOB_ID = JID;

    -- CRITERIA 1
    IF (SYSDATE - HDATE) >= 5 * 365 THEN
        CRIT1 := TRUE;
    ELSE
        RETURN 'NO';
    END IF;

    -- CRITERIA 2
    IF ESAL > (JOB_MIN_SAL + JOB_MAX_SAL) / 2 THEN
        CRIT2 := TRUE;
    ELSE
        RETURN 'NO';
    END IF;

    
    -- CRIETERIA 3
    SELECT COUNT(*) INTO CNT
    FROM EMPLOYEES e
    WHERE E.MANAGER_ID = EID;

    IF CNT > 0 THEN
        RETURN 'YES';
    ELSE
        RETURN 'NO';
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('OK');
        RETURN 'NO';
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('WAKARANAI!!');
        RETURN 'NO';
END IS_READY_FOR_PROMOTION;


BEGIN 
    FOR ID IN 117..124 LOOP
        DBMS_OUTPUT.PUT_LINE(IS_READY_FOR_PROMOTION(ID));
    END LOOP;

END;



-- Write a PL/SQL procedure that updates the salaries of all employees. The new salary for an employee 
-- will be calculated as follows: 
-- New Salary = Old Salary + (Commission Percentage * Old Salary) + 0.1 * (Min 
-- Salary of Employee's Job) + 0.1 * (Average Salary of Employee's Department) 
-- Conditions: 
-- 1. If the employee's work period is 1 year or less, his/her salary will not be updated. (Use the 
-- hire date of the newest employee as today's date instead of SYSDATE). 
-- 2. If the employee’s new salary exceeds the maximum salary for his/her job, set the new salary 
-- to the maximum salary for that job. 
-- The procedure should loop through all employees to update the salary. Handle appropriate exceptions.


CREATE OR REPLACE PROCEDURE UPDATE_SALARIES IS 
    LATEST_DATE DATE;
    NEW_SAL NUMBER;

    CURSOR CR IS 
    SELECT 
        EM.EMPLOYEE_ID,
        EM.HIRE_DATE,
        EM.COMMISSION_PCT,
        EM.SALARY,
        J.JOB_ID,
        J.MIN_SALARY,
        J.MAX_SALARY,                          -- needed to cap salary
        (
            SELECT AVG(E2.SALARY)
            FROM EMPLOYEES E2
            WHERE E2.DEPARTMENT_ID = EM.DEPARTMENT_ID
            -- no GROUP BY needed: correlated subquery returns one value per row
        ) AS AVG_SAL 
    FROM EMPLOYEES EM
    JOIN JOBS_COPY J ON J.JOB_ID = EM.JOB_ID;

BEGIN  
    -- ROWNUM must wrap ORDER BY in a subquery
    SELECT HIRE_DATE INTO LATEST_DATE
    FROM (
        SELECT HIRE_DATE FROM EMPLOYEES
        ORDER BY HIRE_DATE DESC
    )
    WHERE ROWNUM = 1;

    FOR IT IN CR LOOP
        -- Condition 1: skip employees with <= 1 year of service
        IF (LATEST_DATE - IT.HIRE_DATE) <= 365 THEN
            CONTINUE;
        END IF;

        -- NVL handles NULL commission (most employees have no commission)
        NEW_SAL := IT.SALARY
                 + NVL(IT.COMMISSION_PCT, 0) * IT.SALARY
                 + 0.1 * IT.MIN_SALARY
                 + 0.1 * IT.AVG_SAL;

        -- Condition 2: cap at job's max salary
        IF NEW_SAL > (
            SELECT MAX_SALARY 
            FROM JOBS_COPY J1
            WHERE J1.JOB_ID = IT.JOB_ID
        ) THEN
            UPDATE JOBS_COPY J2
            SET J2.MAX_SALARY = NEW_SAL
            WHERE J2.JOB_ID = IT.JOB_ID;
        END IF;

        -- UPDATE syntax: UPDATE <table> SET <col> = <val> WHERE <condition>
        UPDATE employees_copy
        SET SALARY = NEW_SAL
        WHERE EMPLOYEE_ID = IT.EMPLOYEE_ID;

    END LOOP;

    COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('NO DATA FOUND');
    WHEN OTHERS THEN 
        DBMS_OUTPUT.PUT_LINE('ERROR: ' || SQLERRM);

END UPDATE_SALARIES;

 
DROP TABLE employees_copy;
DROP TABLE JOBS_COPY;

CREATE TABLE employees_copy AS 
SELECT * FROM employees; 
COMMIT; 


CREATE TABLE JOBS_copy AS 
SELECT * FROM JOBS; 
COMMIT; 





-- SELECT e.employee_id, e.salary as Old_Salary, ec.salary as New_Salary, 
-- j.min_salary, j.max_salary, e.hire_date 
-- FROM employees_copy ec join employees e  
-- on ec.employee_id=e.employee_id 
-- join jobs j on e.job_id=j.job_id; 


-- ====================================================================================================


-- Question 3.  
-- Create a trigger that activates when an employee’s salary is updated. 
-- Salary decrease>20% → demotion 
 
-- 1 new table: Demotions 
-- (fields:  
-- employee_id,  
-- current_salary,  
-- Status- waiting and done,  
-- date  
-- ) 
 
-- In case of demotion: 
-- 1. No change if that employee is not a manager 
-- 2. If that employee is a manager, switch him/her with the highest-paid employee under him/her. (Do 
-- not switch salaries) 
 
-- No changes in the Job table and the Job_history table are necessary for your ease. All current date is 
-- the hire date of the newest employee instead of SYSDATE. 
 
CREATE TABLE DEMOTIONS (
    EMPLOYEE_ID NUMBER REFERENCES EMPLOYEES(EMPLOYEE_ID),
    CURRENT_SALARY NUMBER,
    CURRENT_STATUS VARCHAR2(10),
    DEMOTION_DATE DATE              -- set explicitly in trigger; Oracle does not allow subqueries in DEFAULT
);

CREATE OR REPLACE TRIGGER DEMOTION_UPDATE 
BEFORE UPDATE OF SALARY 
ON employees_copy
FOR EACH ROW                        -- REQUIRED: enables :OLD and :NEW access
DECLARE
    V_EID           NUMBER  := :OLD.EMPLOYEE_ID;   -- no need for a SELECT; just use :OLD directly
    V_OLD_SAL       NUMBER  := :OLD.SALARY;
    V_NEW_SAL       NUMBER  := :NEW.SALARY;
    EXCHANGE_ID     NUMBER;
    V_SUB_COUNT     NUMBER;
    V_LATEST_DATE   DATE;

    -- Cursor: no parentheses around SELECT; table alias must match
    CURSOR CR IS
        SELECT EMPLOYEE_ID
        FROM employees_copy
        WHERE MANAGER_ID = :OLD.EMPLOYEE_ID;
BEGIN
    -- Use latest hire date as "today"
    SELECT MAX(HIRE_DATE) INTO V_LATEST_DATE FROM EMPLOYEES;

    -- Demotion check: salary dropped by more than 20%
    IF (V_OLD_SAL - V_NEW_SAL) / V_OLD_SAL > 0.2 THEN

        -- Correct INSERT syntax: column list + VALUES
        INSERT INTO DEMOTIONS (EMPLOYEE_ID, CURRENT_SALARY, CURRENT_STATUS, DEMOTION_DATE)
        VALUES (V_EID, V_NEW_SAL, 'WAITING', V_LATEST_DATE);

        -- Can't use SELECT in IF directly; store COUNT in a variable first
        SELECT COUNT(*) INTO V_SUB_COUNT
        FROM employees_copy
        WHERE MANAGER_ID = V_EID;

        IF V_SUB_COUNT > 0 THEN

            -- Get HIGHEST-paid subordinate (DESC, not ASC)
            SELECT EMPLOYEE_ID INTO EXCHANGE_ID
            FROM (
                SELECT EMPLOYEE_ID
                FROM employees_copy
                WHERE MANAGER_ID = V_EID
                ORDER BY SALARY DESC
            )
            WHERE ROWNUM = 1;

            -- Reassign all other subordinates to report to EXCHANGE_ID
            FOR IT IN CR LOOP
                IF IT.EMPLOYEE_ID != EXCHANGE_ID THEN
                    UPDATE employees_copy
                    SET MANAGER_ID = EXCHANGE_ID
                    WHERE EMPLOYEE_ID = IT.EMPLOYEE_ID;
                END IF;
            END LOOP;

            -- EXCHANGE_ID inherits EID's manager (moves up)
            UPDATE employees_copy
            SET MANAGER_ID = (SELECT MANAGER_ID FROM employees_copy WHERE EMPLOYEE_ID = V_EID)
            WHERE EMPLOYEE_ID = EXCHANGE_ID;

            -- Demoted employee now reports to EXCHANGE_ID (moves down)
            UPDATE employees_copy
            SET MANAGER_ID = EXCHANGE_ID
            WHERE EMPLOYEE_ID = V_EID;

        END IF;

    END IF;

    DBMS_OUTPUT.PUT_LINE('Trigger fired for employee: ' || V_EID);

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Error: No data found');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END DEMOTION_UPDATE;
/
