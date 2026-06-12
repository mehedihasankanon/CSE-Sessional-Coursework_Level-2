CREATE TABLE employees_copy AS 
SELECT * FROM employees; 
COMMIT;

-- Write a function named Exchange_Employees that should take two manager_ids as parameters and 
-- exchange the lowest-paying employees under each manager. If there is a tie (i.e., multiple employees 
-- have the same salary), select any one of them. Note that when employees are exchanged, their jobs will 
-- remain the same, but one employee will join the department of the other employee under that employee’s 
-- manager. The salaries of the exchanged employees will be updated. The new salary for each employee 
-- will be increased by 50% of the difference between their original salaries. Print the employee’s 
-- information before and after the exchange and handle appropriate exceptions.

DESCRIBE employees_copy;

SELECT * FROM EMPLOYEES;

CREATE OR REPLACE FUNCTION EXCHANGE_EMPLOYEES(ID1 IN NUMBER, ID2 IN NUMBER) 
RETURN VARCHAR2 IS
    -- Variables for employee 1
    v_emp1_id NUMBER;
    v_emp1_salary NUMBER;
    v_emp1_dept_id NUMBER;
    v_emp1_job_id VARCHAR2(10);
    
    -- Variables for employee 2
    v_emp2_id NUMBER;
    v_emp2_salary NUMBER;
    v_emp2_dept_id NUMBER;
    v_emp2_job_id VARCHAR2(10);
    
    -- Salary difference
    v_salary_diff NUMBER;
    v_new_sal_emp1 NUMBER;
    v_new_sal_emp2 NUMBER;
    
BEGIN
    -- Find lowest-paid employee under manager ID1
    SELECT employee_id, salary, department_id, job_id
    INTO v_emp1_id, v_emp1_salary, v_emp1_dept_id, v_emp1_job_id
    FROM (SELECT employee_id, salary, department_id, job_id
          FROM employees_copy
          WHERE manager_id = ID1
          ORDER BY salary ASC)
    WHERE ROWNUM = 1;
    
    -- Find lowest-paid employee under manager ID2
    SELECT employee_id, salary, department_id, job_id
    INTO v_emp2_id, v_emp2_salary, v_emp2_dept_id, v_emp2_job_id
    FROM (SELECT employee_id, salary, department_id, job_id
          FROM employees_copy
          WHERE manager_id = ID2
          ORDER BY salary ASC)
    WHERE ROWNUM = 1;
    
    -- Display before exchange
    DBMS_OUTPUT.PUT_LINE('--- BEFORE EXCHANGE ---');
    DBMS_OUTPUT.PUT_LINE('Employee ' || v_emp1_id || ': Salary=' || v_emp1_salary || 
                         ', Manager=' || ID1 || ', Department=' || v_emp1_dept_id);
    DBMS_OUTPUT.PUT_LINE('Employee ' || v_emp2_id || ': Salary=' || v_emp2_salary || 
                         ', Manager=' || ID2 || ', Department=' || v_emp2_dept_id);
    
    -- Calculate new salaries: 50% of the salary difference
    v_salary_diff := v_emp2_salary - v_emp1_salary;
    v_new_sal_emp1 := v_emp1_salary + (v_salary_diff * 0.5);
    v_new_sal_emp2 := v_emp2_salary + (-v_salary_diff * 0.5);
    
    -- Update employees: swap managers and update salaries
    UPDATE employees_copy
    SET manager_id = ID2, salary = v_new_sal_emp1
    WHERE employee_id = v_emp1_id;
    
    UPDATE employees_copy
    SET manager_id = ID1, salary = v_new_sal_emp2
    WHERE employee_id = v_emp2_id;
    
    COMMIT;
    
    -- Display after exchange
    DBMS_OUTPUT.PUT_LINE('--- AFTER EXCHANGE ---');
    DBMS_OUTPUT.PUT_LINE('Employee ' || v_emp1_id || ': Salary=' || v_new_sal_emp1 || 
                         ', Manager=' || ID2 || ', Department=' || v_emp1_dept_id);
    DBMS_OUTPUT.PUT_LINE('Employee ' || v_emp2_id || ': Salary=' || v_new_sal_emp2 || 
                         ', Manager=' || ID1 || ', Department=' || v_emp2_dept_id);
    
    RETURN 'Exchange completed successfully';
    
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Error: One or both manager IDs have no employees');
        RETURN 'Error: Manager has no employees';
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        RETURN 'Error occurred: ' || SQLERRM;
END EXCHANGE_EMPLOYEES;

SET SERVEROUTPUT ON;

DECLARE
    v_result VARCHAR2(100);
BEGIN 
    v_result := EXCHANGE_EMPLOYEES(122, 103);
    DBMS_OUTPUT.PUT_LINE('Result: ' || v_result);
END;
/


-- Write a PL/SQL procedure named LOCATION_SALARY_REPORT that performs the following 
-- tasks: 
-- • For each location (city), compute: 
-- o The number of employees working in that city 
-- o The average salary of those employees (rounded to 2 decimal places) 
-- o The job title of the highest-paid employee in that city 
-- • Rank the cities based on: 
-- o Ascending order of the number of employees 
-- o Descending order of average salary (used as a tie-breaker) 
-- • Print the following information for each location: 
-- o Rank 
-- o City Name 
-- o Number of Employees 
-- o Average Salary 
-- o Highest Paying Job Title 
 
-- Make sure to handle exceptions with appropriate messages. 
  
 

CREATE OR REPLACE PROCEDURE LOCATION_SALARY_REPORT IS
    -- This cursor handles the grouping and sorting.
    -- The order here matches the "Ranking" requirements.
    CURSOR city_cursor IS
        SELECT 
            l.city, 
            COUNT(e.employee_id) AS emp_count, 
            ROUND(AVG(e.salary), 2) AS avg_salary
        FROM employees e
        JOIN departments d ON e.department_id = d.department_id
        JOIN locations l   ON d.location_id   = l.location_id
        GROUP BY l.city
        ORDER BY emp_count ASC, avg_salary DESC;

    v_rank        NUMBER := 0;
    v_top_job     VARCHAR2(50);
    v_data_found  BOOLEAN := FALSE;

BEGIN
    -- Print Report Header
    DBMS_OUTPUT.PUT_LINE('=== LOCATION SALARY REPORT ===');
    DBMS_OUTPUT.PUT_LINE(
        RPAD('Rank', 6) || RPAD('City', 20) || 
        RPAD('Employees', 12) || RPAD('Avg Salary', 15) || 
        'Highest Paying Job'
    );
    DBMS_OUTPUT.PUT_LINE(RPAD('-', 75, '-'));

    -- Loop through the sorted cities
    FOR rec IN city_cursor LOOP
        v_rank := v_rank + 1;
        v_data_found := TRUE;

        -- For the current city, find the job title of the highest paid employee
        -- We use a subquery with ROWNUM = 1 to get just the top result
        SELECT job_title INTO v_top_job
        FROM (
            SELECT j.job_title
            FROM employees e2
            JOIN jobs j        ON e2.job_id = j.job_id
            JOIN departments d2 ON e2.department_id = d2.department_id
            JOIN locations l2  ON d2.location_id = l2.location_id
            WHERE l2.city = rec.city
            ORDER BY e2.salary DESC
        )
        WHERE ROWNUM = 1;

        -- Print the row
        DBMS_OUTPUT.PUT_LINE(
            RPAD(v_rank, 6) || 
            RPAD(rec.city, 20) || 
            RPAD(rec.emp_count, 12) || 
            RPAD(TO_CHAR(rec.avg_salary, '999,999.99'), 15) || 
            v_top_job
        );
    END LOOP;

    -- Handle case where no employees/locations exist
    IF NOT v_data_found THEN
        DBMS_OUTPUT.PUT_LINE('No data found to generate report.');
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('An error occurred: ' || SQLERRM);
END LOCATION_SALARY_REPORT;
/

-- Run the procedure
SET SERVEROUTPUT ON;
EXEC LOCATION_SALARY_REPORT;

-- Run the procedure
SET SERVEROUTPUT ON;
EXEC LOCATION_SALARY_REPORT;



-- Question 3. 
 
-- Create a trigger that activates when an employee is transferred to a new department (i.e., when an UPDATE 
-- operation on the department_id is performed in the Employee table). 
 
-- 1 new table: Transfers 
-- (Fields: 
-- employee_id,  
-- employee_working_instead_of_him,  
-- new_department,  
-- current date 
-- ) 



 
-- Conditions: 
-- 1. If that employee had a manager, 
-- a. An employee with the closest salary to him/her under the same manager will work instead of 
-- him/her. The new salary for that work-in-place employee will be equal to previous salary of 
-- that work-in-place employee + 0.5* salary of the transferred employee. 
-- b. His/her new manager in the new department should be the manager with the closest number of 
-- subordinates to his/her previous manager.  
 
-- No changes in the Job table and the Job_history table are necessary for your ease.  

-- ============================================================
-- STEP 1: Create the TRANSFERS table to log each transfer event
-- Fields: who left, who covers, which dept they moved to, when
-- ============================================================
CREATE TABLE TRANSFERS (
    EMPLOYEE_ID              NUMBER REFERENCES EMPLOYEES(EMPLOYEE_ID),
    EMPLOYEE_WORKING_INSTEAD NUMBER REFERENCES EMPLOYEES(EMPLOYEE_ID),
    NEW_DEPARTMENT           NUMBER REFERENCES DEPARTMENTS(DEPARTMENT_ID),
    CURRENT_DATE             DATE DEFAULT SYSDATE   -- BUG 1: CURRENT_DATE is a reserved Oracle keyword
);
/

-- ============================================================
-- STEP 2: Create the trigger
-- Fires BEFORE UPDATE of DEPARTMENT_ID on employees_copy
-- FOR EACH ROW gives us access to :OLD and :NEW values
-- ============================================================
CREATE OR REPLACE TRIGGER TRANSFER_TRIGGER
BEFORE UPDATE OF DEPARTMENT_ID
ON employees_copy
FOR EACH ROW
DECLARE
    -- Old manager of the transferred employee (NULL if no manager)
    V_OLD_MANAGER_ID    NUMBER;
    -- Old salary of the transferred employee (used to calculate peer's new salary)
    V_OLD_SAL           NUMBER  := :OLD.SALARY;

    -- The peer who will cover the transferred employee's work
    V_PEER_ID           NUMBER  := NULL;
    V_PEER_SAL          NUMBER;

    -- Number of subordinates under the old manager (to find closest new manager)
    V_OLD_MGR_SUB_CNT   NUMBER;
    -- The new manager ID chosen for the transferred employee
    V_NEW_MANAGER_ID    NUMBER;

BEGIN
    -- --------------------------------------------------------
    -- Get the old manager of the transferred employee
    -- We use :OLD.EMPLOYEE_ID to refer to the row being updated
    -- --------------------------------------------------------
    SELECT MANAGER_ID INTO V_OLD_MANAGER_ID
    FROM employees_copy
    WHERE EMPLOYEE_ID = :OLD.EMPLOYEE_ID;

    -- --------------------------------------------------------
    -- Condition 1: Only act if the employee had a manager
    -- --------------------------------------------------------
    IF V_OLD_MANAGER_ID IS NOT NULL THEN

        -- ----------------------------------------------------
        -- Part 1a: Find the peer with the CLOSEST salary
        -- Same manager, exclude self, order by ABS difference ASC
        -- ----------------------------------------------------
        SELECT EMPLOYEE_ID, SALARY INTO V_PEER_ID, V_PEER_SAL
        FROM (
            SELECT EMPLOYEE_ID, SALARY
            FROM employees_copy
            WHERE MANAGER_ID = V_OLD_MANAGER_ID
            ORDER BY ABS(SALARY - V_OLD_SAL) DESC  -- BUG 2: should be ASC (closest = smallest difference)
        )
        WHERE ROWNUM = 1;
        -- BUG 3: missing AND EMPLOYEE_ID != :OLD.EMPLOYEE_ID
        -- without this, the employee could be selected as their own cover

        -- Update the peer's salary: peer_salary + 0.5 * transferred employee's salary
        UPDATE employees_copy
        SET SALARY = V_PEER_SAL + (0.5 * V_OLD_SAL)
        WHERE EMPLOYEE_ID = V_PEER_ID;

        -- ----------------------------------------------------
        -- Part 1b: Find the new manager in the new department
        -- The manager whose subordinate count is closest to old manager's count
        -- ----------------------------------------------------

        -- First get how many subordinates the OLD manager had
        SELECT COUNT(*) INTO V_OLD_MGR_SUB_CNT
        FROM employees_copy
        WHERE MANAGER_ID = V_OLD_MANAGER_ID;

        -- BUG 4: SELECT inside IF — invalid PL/SQL; must store COUNT in a variable first (already done above, 
        -- but here we repeat the mistake for the new manager check)
        IF (SELECT COUNT(*) FROM employees_copy WHERE DEPARTMENT_ID = :NEW.DEPARTMENT_ID) > 0 THEN

            -- Find the manager in the new department whose sub count is closest
            SELECT EMPLOYEE_ID INTO V_NEW_MANAGER_ID
            FROM (
                SELECT M.EMPLOYEE_ID
                FROM employees_copy M
                JOIN employees_copy S ON S.MANAGER_ID = M.EMPLOYEE_ID
                WHERE M.DEPARTMENT_ID = :NEW.DEPARTMENT_ID
                GROUP BY M.EMPLOYEE_ID
                ORDER BY ABS(COUNT(S.EMPLOYEE_ID) - V_OLD_MGR_SUB_CNT) ASC
            )
            WHERE ROWNUM = 1;

            -- Update the transferred employee's manager to the new manager
            :NEW.MANAGER_ID := V_NEW_MANAGER_ID;

        ELSE IF V_NEW_MANAGER_ID IS NULL THEN   -- BUG 5: ELSE IF should be ELSIF
            -- If no manager found in new dept, set manager to NULL
            :NEW.MANAGER_ID := NULL;

        END IF;

    END IF;

    -- --------------------------------------------------------
    -- Log the transfer into the TRANSFERS table
    -- V_PEER_ID stays NULL if employee had no manager (condition not met)
    -- --------------------------------------------------------
    INSERT INTO TRANSFERS (EMPLOYEE_ID, EMPLOYEE_WORKING_INSTEAD, NEW_DEPARTMENT, TRANSFER_DATE)
    VALUES (:OLD.EMPLOYEE_ID, V_PEER_ID, :NEW.DEPARTMENT_ID, SYSDATE);

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Error: Could not find peer or manager — ' || SQLERRM);
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
END TRANSFER_TRIGGER;
/

-- Test: transfer employee 101 to department 30
SET SERVEROUTPUT ON;
UPDATE employees_copy SET DEPARTMENT_ID = 30 WHERE EMPLOYEE_ID = 101;
COMMIT;
/
 