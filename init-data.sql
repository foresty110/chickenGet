SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE coupon_history;
TRUNCATE TABLE coupon_stock;
TRUNCATE TABLE coupon_policy;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

SET SESSION cte_max_recursion_depth = 5000;

INSERT INTO coupon_policy (id, name, discount_amount, start_date, end_date)
VALUES (1, '치킨 50% 할인 쿠폰', 5000, '2024-01-01 00:00:00', '2026-12-31 23:59:59');

INSERT INTO coupon_stock (id, coupon_policy_id, total_quantity, remaining_quantity)
VALUES (1, 1, 3000, 3000);

INSERT INTO users (username) 
SELECT CONCAT('user', n)
FROM (
    WITH RECURSIVE seq AS (
        SELECT 1 AS n
        UNION ALL
        SELECT n + 1 FROM seq WHERE n < 3000
    )
    SELECT n FROM seq
) AS numbers;
