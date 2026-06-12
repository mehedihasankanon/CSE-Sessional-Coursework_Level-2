-- 1.  Write a PL/SQL procedure that takes four parameters: employee count 𝒏, 
-- average salary 𝒎, manager id 𝒎_𝒊𝒅 as inputs, and rank 𝒓 as output.  
 
-- The procedure finds each manager who manages more than n employees and 
-- the employees under his/her supervision has average salary less than 𝒎, and 
-- then ranks the managers according to the employees’ average salary under 
-- him and prints those managers’ full names along with their ranks and the 
-- average salaries of the employees. The highest ranked manager (1) manages 
-- the employees with the least average salary. 
 
-- The procedure also returns r, the rank of the manager denoted by m_id. 
 
-- 2.  Write a function that takes two parameters employee_id and manager_id, 
-- and returns the salary rank of the employee working under the manager 
-- (highest salary == rank 1).