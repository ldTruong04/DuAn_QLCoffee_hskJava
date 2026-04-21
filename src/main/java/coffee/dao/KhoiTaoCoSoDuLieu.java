package coffee.dao;

import database.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

public class KhoiTaoCoSoDuLieu {
    public static void initialize() {
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS product (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(120) NOT NULL,
                        category VARCHAR(80) NOT NULL,
                                                price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
                                                description TEXT,
                                                image_path TEXT
                    )
                    """);

                        st.execute("ALTER TABLE product ADD COLUMN IF NOT EXISTS description TEXT");
                        st.execute("ALTER TABLE product ADD COLUMN IF NOT EXISTS image_path TEXT");

            st.execute("""
                    CREATE TABLE IF NOT EXISTS cafe_table (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(40) NOT NULL UNIQUE,
                        occupied BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS employee (
                        id SERIAL PRIMARY KEY,
                        ho_ten VARCHAR(120) NOT NULL,
                        nam_sinh INTEGER NOT NULL DEFAULT 1990,
                        luong NUMERIC(14,2) NOT NULL DEFAULT 0,
                        gioi_tinh VARCHAR(10) NOT NULL DEFAULT 'KHAC',
                        anh_dai_dien TEXT,
                        role VARCHAR(10) NOT NULL,
                        username VARCHAR(60) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL
                    )
                    """);

            st.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS ho_ten VARCHAR(120)");
            st.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS nam_sinh INTEGER NOT NULL DEFAULT 1990");
            st.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS luong NUMERIC(14,2) NOT NULL DEFAULT 0");
            st.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS gioi_tinh VARCHAR(10) NOT NULL DEFAULT 'KHAC'");
            st.execute("ALTER TABLE employee ADD COLUMN IF NOT EXISTS anh_dai_dien TEXT");
                        st.execute("""
                                        DO $$
                                        BEGIN
                                                IF EXISTS (
                                                        SELECT 1
                                                        FROM information_schema.columns
                                                        WHERE table_schema = 'public'
                                                          AND table_name = 'employee'
                                                          AND column_name = 'name'
                                                ) THEN
                                                        EXECUTE 'UPDATE employee
                                                                         SET ho_ten = COALESCE(NULLIF(ho_ten, ''''''), name, username),
                                                                                 gioi_tinh = COALESCE(gioi_tinh, ''KHAC'')
                                                                         WHERE ho_ten IS NULL OR ho_ten = '''''' OR gioi_tinh IS NULL';
                                                ELSE
                                                        UPDATE employee
                                                        SET ho_ten = COALESCE(NULLIF(ho_ten, ''), username),
                                                                gioi_tinh = COALESCE(gioi_tinh, 'KHAC')
                                                        WHERE ho_ten IS NULL OR ho_ten = '' OR gioi_tinh IS NULL;
                                                END IF;
                                        END
                                        $$;
                                        """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS invoice (
                        id SERIAL PRIMARY KEY,
                        table_id INTEGER NOT NULL REFERENCES cafe_table(id),
                        employee_id INTEGER NOT NULL REFERENCES employee(id),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        paid BOOLEAN NOT NULL DEFAULT FALSE
                    )
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS invoice_item (
                        invoice_id INTEGER NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
                        product_id INTEGER NOT NULL REFERENCES product(id),
                        quantity INTEGER NOT NULL CHECK (quantity > 0),
                        unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price >= 0),
                        PRIMARY KEY (invoice_id, product_id)
                    )
                    """);

            st.execute("""
                    INSERT INTO employee(ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password)
                    SELECT 'Quản lý', 1985, 15000000, 'NAM', NULL, 'ADMIN', 'admin', 'admin123'
                    WHERE NOT EXISTS (SELECT 1 FROM employee WHERE username = 'admin')
                    """);

            st.execute("""
                    INSERT INTO employee(ho_ten, nam_sinh, luong, gioi_tinh, anh_dai_dien, role, username, password)
                    SELECT 'Nhân viên A', 1998, 8000000, 'NU', NULL, 'STAFF', 'staff', 'staff123'
                    WHERE NOT EXISTS (SELECT 1 FROM employee WHERE username = 'staff')
                    """);

            st.execute("""
                    INSERT INTO product(name, category, price, description, image_path)
                    SELECT 'Cà phê đen', 'Cà phê', 25000, 'Cà phê đậm vị', NULL
                    WHERE NOT EXISTS (SELECT 1 FROM product)
                    """);
            st.execute("""
                    INSERT INTO product(name, category, price, description, image_path)
                    SELECT 'Cà phê sữa', 'Cà phê', 30000, 'Cà phê sữa béo thơm', NULL
                    WHERE (SELECT COUNT(*) FROM product) = 1
                    """);
            st.execute("""
                    INSERT INTO product(name, category, price, description, image_path)
                    SELECT 'Trà đào', 'Trà', 35000, 'Trà đào thanh mát', NULL
                    WHERE (SELECT COUNT(*) FROM product) = 2
                    """);

            for (int i = 1; i <= 8; i++) {
                st.execute("INSERT INTO cafe_table(name, occupied) SELECT 'B" + i
                        + "', FALSE WHERE NOT EXISTS (SELECT 1 FROM cafe_table WHERE name = 'B" + i + "')");
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể khởi tạo CSDL: " + e.getMessage(), e);
        }
    }
}
