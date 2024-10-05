#1

SELECT d.department_name "department_name",

       (case 
           when COUNT(e.employee_id) = 0 then 'N/A'
           else to_char(COUNT(e.employee_id))
       end) "emp_cnt",
       
       (case
           when COUNT(DISTINCT e.job_id) = 0 then 'N/A'
           else to_char(COUNT(DISTINCT e.job_id))
       end) "job_cnt",
       
       NVL(to_char(SUM(e.salary)), 'N/A') "sum_sal",
       NVL(to_char(MIN(e.salary) + MAX(e.salary)), 'N/A') "sum_min_max",
       
       listagg(e.phone_number, ', ')WITHIN GROUP (ORDER BY e.salary ASC) AS "phone_numbers"
       
  FROM departments d
  LEFT JOIN employees e
    ON e.department_id = d.department_id
  LEFT JOIN locations l
    ON l.location_id = d.location_id
  LEFT JOIN countries c
    ON l.country_id = c.country_id
WHERE lower(c.country_name) like (lower(c.country_id) || '%')
 
GROUP BY d.department_name, c.country_id, c.country_name

HAVING
LENGTH(d.department_name) - LENGTH(REPLACE(d.department_name, ' ', '')) = 0

ORDER BY c.country_id ASC, c.country_name DESC;

#2

SELECT j.job_id    as "job_id ",
       j.job_title as "name",
       
       NVL(to_char(SUM(CASE
          WHEN mod(e.employee_id, 2) = 1 THEN 1
          ELSE null
         END)), 'N/A') as "emp_cnt",
       
        NVL((select to_char(avg(tmp.salary))
          from employees tmp
         where tmp.job_id = j.job_id
           and mod(tmp.employee_id, 2) = 0), 'N/A') as "avg_sal",
       
       NVL((select to_char(count(tmp.employee_id))
          from employees tmp
         where tmp.job_id = j.job_id
           and tmp.salary = 
           (select max(tmp2.salary)
           from employees tmp2
           where tmp2.job_id = tmp.job_id)), 'N/A') "mx_cnt"

  FROM jobs j
  LEFT JOIN employees e
    on j.job_id = e.job_id
 group by j.job_id, j.job_title
 
having length(j.job_id) >= 4 
and max(j.max_salary) - min(j.min_salary) != 
(select max(tmp.max_salary - tmp.min_salary) from jobs tmp)

 order by (case
            when lower(j.job_id) like '%it%' 
                 then 1
                 else 2
          end),
          j.job_id ASC;
