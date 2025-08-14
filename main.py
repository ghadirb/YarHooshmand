import threading
from kivy.app import App
from kivy.lang import Builder
from kivy.clock import Clock
from kivy.properties import StringProperty, BooleanProperty
from kivy.utils import platform
from kivy.core.window import Window

from app.keys_loader import KeysManager
from app.ai_client import SmartClient

try:
    # اعلان محلی (روی اندروید جواب می‌دهد)
    from plyer import notification
except Exception:
    notification = None


KV = """
#:import partial functools.partial
ScreenManager:
    id: sm
    Screen:
        name: "home"
        BoxLayout:
            orientation: "vertical"
            padding: dp(12)
            spacing: dp(8)

            BoxLayout:
                size_hint_y: None
                height: self.minimum_height
                spacing: dp(8)
                Label:
                    text: "یار هوشمند"
                    font_size: "20sp"
                    bold: True
                Widget:
                Label:
                    text: root_app.status_text
                    color: (0,1,0,1) if root_app.keys_ok else (1,0,0,1)
                    font_size: "12sp"

            TextInput:
                id: remind_text
                hint_text: "یادآور یا پیام را بنویس..."
                multiline: True
                size_hint_y: None
                height: dp(140)

            BoxLayout:
                size_hint_y: None
                height: dp(44)
                spacing: dp(8)
                TextInput:
                    id: seconds
                    hint_text: "ثانیه تا اعلان (مثلاً 30)"
                    input_filter: "int"
                Button:
                    text: "ثبت یادآور"
                    on_release: root_app.schedule_reminder(remind_text.text, seconds.text)

            BoxLayout:
                size_hint_y: None
                height: dp(44)
                spacing: dp(8)
                TextInput:
                    id: user_msg
                    hint_text: "سؤال یا دستور فارسی برای هوش…"
                Button:
                    text: "پرسش"
                    on_release: root_app.ask_ai(user_msg.text)

            ScrollView:
                size_hint_y: 1
                do_scroll_x: False
                do_scroll_y: True
                GridLayout:
                    id: chat
                    cols: 1
                    size_hint_y: None
                    height: self.minimum_height
                    spacing: dp(6)
"""

class RootApp(App):
    status_text = StringProperty("در حال خواندن کلیدها…")
    keys_ok = BooleanProperty(False)

    def build(self):
        if platform in ("win", "linux", "macosx"):
            Window.size = (420, 720)
        root = Builder.load_string(KV)
        # نگهداری رفرنسی برای kv
        root.root_app = self  # type: ignore
        Clock.schedule_once(self._init_keys, 0)
        return root

    def _add_chat_bubble(self, text, who="شما"):
        from kivy.uix.label import Label
        lbl = Label(text=f"[b]{who}:[/b] {text}", markup=True, size_hint_y=None)
        lbl.bind(texture_size=lambda *a: setattr(lbl, "height", lbl.texture_size[1] + 12))
        self.root.ids.chat.add_widget(lbl)

    def _init_keys(self, *args):
        # لینک مستقیم گوگل‌درایو که دادی:
        drive_url = "https://drive.google.com/uc?export=download&id=17iwkjyGcxJeDgwQWEcsOdfbOxOah_0u0"
        self.keys = KeysManager(remote_url=drive_url)
        self.keys_ok = self.keys.refresh_from_remote()
        self.status_text = "اتصال کلید: سالم" if self.keys_ok else "اتصال کلید: خطا"
        self.client = SmartClient(self.keys)

    def schedule_reminder(self, text, seconds_str):
        try:
            s = int(seconds_str or "0")
            if s <= 0 or not text.strip():
                self._add_chat_bubble("زمان/متن نامعتبر است.", "سیستم")
                return
        except ValueError:
            self._add_chat_bubble("عدد ثانیه نامعتبر است.", "سیستم")
            return

        def fire(_dt):
            # اعلان
            if notification:
                notification.notify(
                    title="یادآور",
                    message=text[:120],
                    timeout=5
                )
            self._add_chat_bubble(f"یادآور: {text}", "اعلان")

        self._add_chat_bubble(f"یادآور ثبت شد و {s} ثانیه دیگر اعلام می‌شود.", "سیستم")
        Clock.schedule_once(fire, s)

    def ask_ai(self, user_text):
        if not user_text.strip():
            return
        self._add_chat_bubble(user_text, "شما")

        def worker():
            try:
                reply = self.client.chat(user_text)
            except Exception as e:
                reply = f"اشکال در ارتباط با مدل: {e}"
            Clock.schedule_once(lambda _dt: self._add_chat_bubble(reply, "هوشمند"), 0)

        threading.Thread(target=worker, daemon=True).start()


if __name__ == "__main__":
    RootApp().run()
