
import time, os, sys, json
from datetime import datetime
BASE = os.path.dirname(os.path.dirname(__file__))
sys.path.insert(0, BASE)
from utils import db, location as loc_utils
from plyer import notification

db.init_db()
CHECK_SEC = 30

def post_notification(title, message):
    try:
        notification.notify(title=title, message=message, timeout=10)
    except Exception as e:
        print("Notify error:", e)

def check_once():
    # naive: get current location (placeholder returns None)
    cur = loc_utils.get_current_location()
    if not cur:
        return
    lat_cur, lon_cur = cur
    for uid,_ in db.list_users():
        for (rid, title, body, due_ts, repeat, tag, lat, lon, radius, calendar, done) in db.list_reminders(uid, include_done=False):
            if lat is not None and lon is not None:
                d = loc_utils.haversine(lat_cur, lon_cur, lat, lon)
                if d <= (radius or 100):
                    post_notification(f"[Location] {title}", body or "")

def main():
    while True:
        try:
            check_once()
        except Exception as e:
            print("Loop error:", e)
        time.sleep(CHECK_SEC)

if __name__ == "__main__":
    main()
