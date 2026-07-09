-- Eski rasm URL larni tuzatish
-- "products/uuid.jpg" → "/uploads/products/uuid.jpg"
-- "/products/uuid.jpg" → "/uploads/products/uuid.jpg"

BEGIN;

-- Products jadvalini yangilash
UPDATE products 
SET image_url = '/uploads/products/' || regexp_replace(image_url, '^/?products/', '')
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND image_url NOT LIKE '/uploads/%'
  AND image_url NOT LIKE 'http%';

-- Shops jadvalini yangilash  
UPDATE shops 
SET image_url = '/uploads/products/' || regexp_replace(image_url, '^/?products/', '')
WHERE image_url IS NOT NULL 
  AND image_url != ''
  AND image_url NOT LIKE '/uploads/%'
  AND image_url NOT LIKE 'http%';

-- Natijani tekshirish
SELECT 'products' as table_name, image_url FROM products WHERE image_url IS NOT NULL LIMIT 5
UNION ALL
SELECT 'shops', image_url FROM shops WHERE image_url IS NOT NULL LIMIT 5;

COMMIT;
