# -*- coding: utf-8 -*-
from kivy.app import App
from kivy.lang import Builder
from kivy.clock import Clock
from kivy.uix.tabbedpanel import TabbedPanel
from kivy.properties import StringProperty, BooleanProperty
from kivy.utils import platform
import re, time, requests

try:
    from plyer import notification
except Exception:
    notification = None

KV = r"""
<TopBar@BoxLayout>:
    size_hint_y: None
    height: "48dp"
    padding: "8dp"
    spacing: "8dp"
    canvas.before:
        Color:
            rgba: (.12, .12, .12, 1)
        Rectangle:
            pos: self.pos
            size: self.size
    Label:
        text: "یارهوشمند - یادآور"
        bold: True

<RootTabs>:
    do_default_tab: False

    TabbedPanelItem:
        text: "ساده"
        BoxLayout:
            orientation: "vertical"
            padding: "12dp"
            spacing: "8dp"
            TopBar:
            BoxLayout:
                size_hint_y: None
                height: "36dp"
                spacing: "6dp"
                Label:
                    text: "پیام:"
                    size_hint_x: None
                    width: "60dp"
                TextInput:
                    id: msg
                    hint_text: "مثلاً: آب بخور"
            BoxLayout:
                size_hint_y: None
                height: "36dp"
                spacing: "6dp"
                Label:
                    text: "ثانیه:"
                    size_hint_x: None
                    width: "60dp"
                TextInput:
                    id: sec
                    input_filter: "int"
                    hint_text: "مثلاً 10"
            BoxLayout:
                size_hint_y: None
                height: "40dp"
                spacing: "6dp"
                Button:
                    text: "ثبت یادآور"
                    on_release: app.schedule_simple(msg.text, sec.text)
                Button:
                    text: "تست اعلان"
                    on_release: app.test_notify()
            Label:
                text: app.simple_status
                color: (0.2,1,0.2,1) if "موفق" in app.simple_status else (1,0.3,0.3,1)

    TabbedPanelItem:
        text: "هوشمند"
        BoxLayout:
            orientation: "vertical"
            padding: "12dp"
            spacing: "8dp"
            TopBar:
            BoxLayout:
                size_hint_y: None
                height: "36dp"
                spacing: "6dp"
                Label:
                    text: "دستور:"
                    size_hint_x: None
                    width: "60dp"
                TextInput:
                    id: ai_cmd
                    hint_text: "مثلاً: 10 دقیقه دیگر یادم بنداز آب بخور"
            BoxLayout:
                size_hint_y: None
                height: "40dp"
                spacing: "6dp"
                Button:
                    text: "تجزیه و ثبت یادآور"
                    on_release: app.parse_and_schedule(ai_cmd.text)
                Button:
                    text: "به‌روزرسانی کلیدها"
                    on_release: app.refresh_keys_async()
            Label:
                text: app.ai_status
                color: (0.2,1,0.2,1) if "موفق" in app.ai_status else (1,0.6,0.2,1)
"""

class RootTabs(TabbedPanel):
    pass

class YarApp(App):
    simple_status = StringProperty("آماده.")
    ai_status = StringProperty("حالت هوشمند: آماده.")
    ai_connected = BooleanProperty(False)

    def build(self):
        self.title = "YarHooshmand"
        self.keys = {"provider": None, "api_key": None}
        self._request_android_permissions_if_any()
        Builder.load_string(KV)
        return RootTabs()

    def _request_android_permissions_if_any(self):
        if platform == "android":
            try:
                from android.permissions import request_permissions, Permission
                request_permissions([Permission.POST_NOTIFICATIONS])
            except Exception:
                pass

    def _notify(self, title, message):
        try:
            if notification:
                notification.notify(title=title, message=message, timeout=5)
                return True
        except Exception:
            pass
        return False

    def test_notify(self):
        ok = self._notify("تست اعلان", "این یک اعلان تستی است.")
        self.simple_status = "تست: موفق." if ok else "تست: plyer در دسترس نیست."

    def schedule_simple(self, message, sec_text):
        try:
            sec = int(sec_text.strip())
        except Exception:
            self.simple_status = "لطفاً عدد ثانیه را درست وارد کن."
            return
        if sec <= 0:
            self.simple_status = "ثانیه باید بیشتر از صفر باشد."
            return

        def fire(_dt):
            ok = self._notify("یادآور", message or "زمان انجام کار!")
            self.simple_status = "اعلان ارسال شد." if ok else "اعلان ارسال نشد (plyer در دسترس نیست)."

        Clock.schedule_once(fire, sec)
        self.simple_status = f"یادآور با موفقیت برای {sec} ثانیه بعد ثبت شد."

    def parse_and_schedule(self, cmd):
        if not cmd or not cmd.strip():
            self.ai_status = "متن خالی است."
            return

        s = cmd.strip().replace("‌", " ")

        # ساعت HH:MM
        m = re.search(r"ساعت\s+(\d{1,2})[:：](\d{2})", s)
        if m:
            hh = int(m.group(1)); mm = int(m.group(2))
            now = time.localtime()
            target = list(now)
            target[3] = hh; target[4] = mm; target[5] = 0
            ts_target = time.mktime(tuple(target))
            delta = int(ts_target - time.time())
            if delta < 5:
                delta += 24*3600
            if delta < 5:
                delta = 5
            self._schedule_ai(cmd, delta)
            return

        # "در 10 دقیقه" / "10 دقیقه دیگر" / "در 2 ساعت" / "30 ثانیه"
        m = re.search(r"(?:در\s+)?(\d+)\s*(ثانیه|دقیقه|ساعت)\s*(?:دیگر)?", s)
        if m:
            n = int(m.group(1))
            unit = m.group(2)
            factor = 1 if unit == "ثانیه" else (60 if unit == "دقیقه" else 3600)
            seconds = max(n * factor, 5)
            self._schedule_ai(cmd, seconds)
            return

        self.ai_status = "الگوی زمان پیدا نشد. نمونه: «10 دقیقه دیگر» یا «ساعت 14:30»"

    def _schedule_ai(self, cmd, seconds):
        message = self._extract_message(cmd)
        def fire(_dt):
            ok = self._notify("یادآور هوشمند", message)
            self.ai_status = "اعلان هوشمند ارسال شد." if ok else "اعلان ارسال نشد (plyer در دسترس نیست)."
        Clock.schedule_once(fire, seconds)
        self.ai_status = f"یادآور هوشمند برای {seconds} ثانیه بعد ثبت شد."

    def _extract_message(self, cmd):
        m = re.search(r"یادم\s*بنداز\s*(.+)$", cmd)
        if m:
            return m.group(1).strip()
        m = re.search(r"که\s+(.+)$", cmd)
        if m:
            return m.group(1).strip()
        return "زمان انجام کار!"

    def refresh_keys_async(self):
        self.ai_status = "درحال دریافت کلیدها از Google Drive..."
        import threading
        threading.Thread(target=self._refresh_keys, daemon=True).start()

    def _refresh_keys(self):
        file_id = "17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"
        url = f"https://drive.google.com/uc?export=download&id={file_id}"
        try:
            r = requests.get(url, timeout=15)
            if r.status_code == 200 and r.text.strip():
                self.keys = {"raw": r.text.strip()}
                self.ai_connected = True
                self.ai_status = "کلیدها با موفقیت دریافت شدند."
            else:
                self.ai_connected = False
                self.ai_status = f"دانلود نشد (HTTP {r.status_code})."
        except Exception as e:
            self.ai_connected = False
            self.ai_status = f"دانلود کلیدها ناموفق بود: {e}"

class Root(RootTabs):
    pass

if __name__ == "__main__":
    YarApp().run()
