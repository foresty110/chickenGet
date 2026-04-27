#!/bin/bash

# 1. SQL 파일의 앞부분 작성
cat <<EOF > init-data.sql
-- 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE coupon_history;
TRUNCATE TABLE coupon_stock;
TRUNCATE TABLE coupon_policy;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. 쿠폰 정책 생성 (ID: 1)
INSERT INTO coupon_policy (id, name, discount_amount, start_date, end_date)
VALUES (1, '치킨 50% 할인 쿠폰', 5000, '2024-01-01 00:00:00', '2026-12-31 23:59:59');

-- 2. 쿠폰 재고 생성 (정책 1번에 대해 100개 할당)
INSERT INTO coupon_stock (id, coupon_policy_id, total_quantity, remaining_quantity)
VALUES (1, 1, 100, 100);

-- 3. 유저 100명 생성 시작
EOF

# 2. 루프를 사용하여 유저 데이터 추가
echo "INSERT INTO users (username) VALUES " >> init-data.sql
for i in {1..99}
do
    echo "('user$i')," >> init-data.sql
done
echo "('user100');" >> init-data.sql

echo "SQL 파일 생성 완료: init-data.sql"

# 3. MySQL 컨테이너에 적용
docker exec -i \$(docker ps -qf "name=mysql") mysql -uroot -proot gachadb < init-data.sql

echo "DB 데이터 초기화 및 주입 완료!"
