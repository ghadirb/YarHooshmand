
import sqlite3, os, time, shutil
DB_PATH = os.path.join(os.path.dirname(__file__), "..", "data", "app.db")
os.makedirs(os.path.join(os.path.dirname(__file__), "..", "data"), exist_ok=True)

def get_conn():
    return sqlite3.connect(DB_PATH, check_same_thread=False)

def init_db():
    with get_conn() as conn:
        c = conn.cursor()
        c.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL
        )""")
        c.execute("""
        CREATE TABLE IF NOT EXISTS chats (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            role TEXT NOT NULL,
            text TEXT NOT NULL,
            ts INTEGER NOT NULL,
            FOREIGN KEY(user_id) REFERENCES users(id)
        )""")
        c.execute("""
        CREATE TABLE IF NOT EXISTS reminders (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            title TEXT NOT NULL,
            body TEXT,
            due_ts INTEGER NOT NULL,
            repeat TEXT DEFAULT 'none',
            tag TEXT DEFAULT '',
            lat REAL DEFAULT NULL,
            lon REAL DEFAULT NULL,
            radius INTEGER DEFAULT 100,
            calendar INT DEFAULT 0,
            created_ts INTEGER NOT NULL,
            done INTEGER DEFAULT 0,
            FOREIGN KEY(user_id) REFERENCES users(id)
        )""")
        c.execute("""
        CREATE TABLE IF NOT EXISTS providers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL,
            api_key TEXT
        )""")
        conn.commit()
        c.execute("INSERT OR IGNORE INTO users(name) VALUES ('Default')")
        for p in ("openai","huggingface","cohere"):
            c.execute("INSERT OR IGNORE INTO providers(name, api_key) VALUES (?, '')", (p,))
        conn.commit()

def get_default_user_id():
    with get_conn() as conn:
        c = conn.cursor()
        c.execute("SELECT id FROM users WHERE name='Default'")
        r = c.fetchone()
        return r[0] if r else 1

def list_users():
    with get_conn() as conn:
        return conn.execute("SELECT id,name FROM users ORDER BY id").fetchall()

def add_user(name):
    with get_conn() as conn:
        conn.execute("INSERT INTO users(name) VALUES (?)", (name,))
        conn.commit()

def add_chat(user_id, role, text):
    with get_conn() as conn:
        conn.execute("INSERT INTO chats(user_id, role, text, ts) VALUES (?,?,?,?)",
                     (user_id, role, text, int(time.time())))
        conn.commit()

def list_chats(user_id, limit=200, query=None):
    with get_conn() as conn:
        if query:
            return conn.execute("SELECT role, text, ts FROM chats WHERE user_id=? AND text LIKE ? ORDER BY id DESC LIMIT ?",
                            (user_id, f"%{query}%", limit)).fetchall()[::-1]
        return conn.execute("SELECT role, text, ts FROM chats WHERE user_id=? ORDER BY id DESC LIMIT ?",
                            (user_id, limit)).fetchall()[::-1]

def set_api_key(name, key):
    with get_conn() as conn:
        conn.execute("UPDATE providers SET api_key=? WHERE name=?", (key, name))
        conn.commit()

def get_api_key(name):
    with get_conn() as conn:
        r = conn.execute("SELECT api_key FROM providers WHERE name=?", (name,)).fetchone()
        return r[0] if r else ""

def add_reminder(user_id, title, body, due_ts, repeat='none', tag='', lat=None, lon=None, radius=100, calendar=0):
    with get_conn() as conn:
        cur = conn.execute("""
        INSERT INTO reminders(user_id,title,body,due_ts,repeat,tag,lat,lon,radius,calendar,created_ts,done)
        VALUES (?,?,?,?,?,?,?,?,?,?,?,0)""",
        (user_id, title, body, int(due_ts), repeat, tag, lat, lon, int(radius), int(calendar), int(time.time())))
        conn.commit()
        return cur.lastrowid

def list_reminders(user_id, include_done=True, tag=None):
    q = "SELECT id,title,body,due_ts,repeat,tag,lat,lon,radius,calendar,done FROM reminders WHERE user_id=? "
    if not include_done:
        q += "AND done=0 "
    if tag:
        q += "AND tag=? "
    q += "ORDER BY due_ts ASC"
    with get_conn() as conn:
        if tag:
            return conn.execute(q, (user_id, tag)).fetchall()
        return conn.execute(q, (user_id,)).fetchall()

def update_reminder(rid, **kwargs):
    if not kwargs: return
    cols = ", ".join([f"{k}=?" for k in kwargs.keys()])
    vals = list(kwargs.values())
    vals.append(rid)
    with get_conn() as conn:
        conn.execute(f"UPDATE reminders SET {cols} WHERE id=?", vals)
        conn.commit()

def delete_reminder(rid):
    with get_conn() as conn:
        conn.execute("DELETE FROM reminders WHERE id=?", (rid,))
        conn.commit()

def backup_db(dst_path):
    if os.path.exists(DB_PATH):
        shutil.copy2(DB_PATH, dst_path)
        return True
    return False

def restore_db(src_path):
    if os.path.exists(src_path):
        shutil.copy2(src_path, DB_PATH)
        return True
    return False
