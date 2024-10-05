#1
SELECT 
(e.first_name || ' ''-'' ' || e.last_name) as "F and L",
NVL2(dep.department_name, dep.department_name, 'X') "DEP",
men.first_name as manFirstName,
men.first_name as "manager name",
TO_CHAR(men.salary, '$99,999.00') "manager salary", --menegeris xelpass vbewdav, ar iyo dakonkretebuli romlis undodat
menmen.first_name as "manger manager name", 
TO_CHAR(menmen.salary, '$99,999.00') "manager manager salary"

FROM employees e 
JOIN departments dep on e.department_id = dep.department_id
JOIN employees men ON e.manager_id = men.employee_id
JOIN employees menmen on men.manager_id = menmen.employee_id

WHERE lower(menmen.first_name) like '%a%'
AND (mod(to_number(menmen.salary), 17) = 0);


#2
select 
(e.first_name || ' ' || e.last_name) as "full_name",
to_char(e.salary,'fm$999,999,999.00') as salary,

CASE
  WHEN e.email is null or instr(e.email, '@') = 0 then 'not valid'
  ELSE e.email
END "EMAIL", 

(to_number(to_char(sysdate, 'fmMM')) - 1) * e.salary AS "taken salary", 
CASE
  WHEN e.commission_pct is null THEN 'no com'
  ELSE to_char(e.commission_pct, 'fm0.00') 
END
FROM employees e
WHERE 
(e.department_id = 50 OR (months_between(sysdate, e.hire_date)/12 >= 5))
AND e.phone_number like '515%'
AND e.job_id not in ('IT_PROG','PU_CLERK')
AND to_number(e.salary) BETWEEN 5000 and 10000
AND e.manager_id is not null
ORDER BY
e.salary DESC,
e.hire_date ASC;



#3
SELECT
SUBSTR(j.job_id, 1, instr(j.job_id, '_') - 1) as pref,
COUNT (e.employee_id) as count, 
(case
       when count(e.employee_id) != count(*) then 'N'
       else 'Y' end
) as "Yn",

ROUND(AVG(e.salary), 2) avrg,
COUNT(case when TO_CHAR(e.hire_date, 'fmYYYY') = '2002' then 1 else null end) "cnt_2002"

FROM jobs j
LEFT JOIN employees e
ON j.job_id = e.job_id

GROUP BY SUBSTR(j.job_id, 1, instr(j.job_id, '_') - 1)
ORDER BY 
(CASE 
      WHEN pref LIKE 'IT%' then 1
      WHEN pref LIKE 'MK%' then 2
      ELSE 3 
END) ASC, avrg DESC;

