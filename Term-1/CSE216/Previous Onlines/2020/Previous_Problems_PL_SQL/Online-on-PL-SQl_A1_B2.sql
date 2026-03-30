-- 1. Write a PL/SQL block that increases the salary (by 15%) of each manager in the 
-- Employee table who has worked for 24 years or more. Output the last name, department 
-- id, old salary, and new salary for the managers whose salary was updated. Also, output 
-- the total number of updates in the table. 
SET SERVEROUTPUT ON;

DECLARE
    -- Variable to track total number of updates
    v_update_count NUMBER := 0;
    -- declare a new variable type:
    -- new_var old_var%TYPE
    v_new_salary   employees.salary%TYPE;
BEGIN
    -- Loop through employees who are managers and have worked 24+ years
    -- Note: Manager jobs in HR schema usually end in 'MAN' or 'MGR'
    FOR r_emp IN (
        SELECT 
            employee_id, 
            last_name, 
            department_id, 
            salary, 
            hire_date
        FROM employees
        WHERE (job_id LIKE '%MAN%' OR job_id LIKE '%MGR%')
          AND MONTHS_BETWEEN(SYSDATE, hire_date) / 12 >= 24
    ) 
    LOOP
        -- 1. Calculate the new salary
        v_new_salary := r_emp.salary * 1.15;

        -- 2. Update the table
        UPDATE employees
        SET salary = v_new_salary
        WHERE employee_id = r_emp.employee_id;

        -- 3. Increment the counter
        v_update_count := v_update_count + 1;

        -- 4. Output individual details
        DBMS_OUTPUT.PUT_LINE(
            'Updated: ' || r_emp.last_name || 
            ' | Dept: ' || r_emp.department_id || 
            ' | Old Sal: $' || TO_CHAR(r_emp.salary, '99,999.99') || 
            ' | New Sal: $' || TO_CHAR(v_new_salary, '99,999.99')
        );
    END LOOP;

    -- 5. Output total number of updates
    DBMS_OUTPUT.PUT_LINE('-----------------------------------------');
    DBMS_OUTPUT.PUT_LINE('Total number of updates: ' || v_update_count);
    
    -- Optional: COMMIT the changes
    -- COMMIT;
END;
/

SELECT * FROM JOBS;
/

 
-- 2. Write a PL/ SQL procedure called INTERCHANGE_SALARY that takes two employee 
-- id EID1 and EID2 as input and interchange the salary of these two employees. You need 
-- to print the previous and new salary of these two employees. Be sure to handle 
-- appropriate exceptions. Finally, write a PL/SQL block to call the procedure. 

CREATE OR REPLACE PROCEDURE INTERCHANGE_SALARY (
    EID1 IN EMPLOYEES.EMPLOYEE_ID%TYPE,
    EID2 IN EMPLOYEES.EMPLOYEE_ID%TYPE
) IS 
    V_SAL_1 NUMBER;
    V_SAL_2 NUMBER;
BEGIN 
    SELECT SALARY INTO V_SAL_1 FROM EMPLOYEES WHERE EMPLOYEE_ID = EID1 ;
    SELECT SALARY INTO V_SAL_2 FROM EMPLOYEES WHERE EMPLOYEE_ID = EID2 ;

    UPDATE EMPLOYEES
    SET SALARY = V_SAL_2
    WHERE EMPLOYEE_ID = EID1;

    UPDATE EMPLOYEES
    SET SALARY = V_SAL_1
    WHERE EMPLOYEE_ID = EID2;

    -- OUTPUT CHANGES HERE

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('YEAH BRO NONE FOUND');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('YEAH BRO SOME OTHER ISSUE');
END;

BEGIN
    INTERCHANGE_SALARY(221, 237);
END;
/



-- 3. Write a PL/SQL function which finds the employees working in the ACCOUNTING 
-- department for more than 20 years, and outputs a message for each of them. The number 
-- of employees who fulfill the criteria should be counted and returned. Then, write a 
-- PL/SQL block to call the function and output the count. 

SET SERVEROUTPUT ON;

CREATE OR REPLACE FUNCTION COUNT_SENIOR_ACCOUNTANTS 
RETURN NUMBER 
IS
    -- Variable to hold the count
    v_counter NUMBER := 0;
BEGIN
    -- We use a cursor-for-loop to iterate through employees in the Accounting dept
    FOR r_emp IN (
        SELECT e.last_name, e.hire_date, d.department_name
        FROM employees e
        JOIN departments d ON e.department_id = d.department_id
        WHERE UPPER(d.department_name) = UPPER('Accounting')
          AND MONTHS_BETWEEN(SYSDATE, e.hire_date) / 12 > 20
    ) 
    LOOP
        -- 1. Output the message for each employee found
        DBMS_OUTPUT.PUT_LINE('Senior Staff Found: ' || r_emp.last_name || 
                             ' (Hired: ' || TO_CHAR(r_emp.hire_date, 'DD-MON-YYYY') || ')');
        
        -- 2. Increment the counter
        v_counter := v_counter + 1;
    END LOOP;

    -- 3. Return the final count
    RETURN v_counter;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('An error occurred: ' || SQLERRM);
        RETURN 0;
END;
/
 
 
-- 4. Copy the contents of the employee table to create a new table titled “Employee_2”. 
-- Create a Trigger called NO_DEC to ensure salary is not decreased for any employees in 
-- the Employee_2 table. 
-- (Hints: Think about the trigger? Will it be a row/statement level trigger? Will it called 
-- before/after on which operation (INSERT/UPDATE/DELETE) ? )  
-- [Use RAISE_APPLICATION_ERROR to throw an error if salary is decreased with 
-- appropriate message] 

CREATE OR REPLACE TRIGGER NO_DEC
BEFORE UPDATE OF salary ON Employee_2
FOR EACH ROW
BEGIN
    -- Check if the new salary is less than the old salary
    IF :NEW.salary < :OLD.salary THEN
        -- RAISE_APPLICATION_ERROR(error_number, message)
        -- Error numbers must be between -20000 and -20999
        RAISE_APPLICATION_ERROR(-20001, 'Transaction Denied: Salary decrease is not allowed for employee ' || :OLD.last_name);
    END IF;
END;
/

