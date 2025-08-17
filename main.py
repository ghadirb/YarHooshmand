
from kivy.lang import Builder
from kivy.utils import platform
from kivymd.app import MDApp
from kivy.uix.snackbar import Snackbar
import os, json, importlib.util, glob, time

from utils import db, location as loc_utils, wake_word

BASE = os.path.dirname(__file__)
SETTINGS = {}
try:
    with open(os.path.join(BASE, "settings.json"), "r", encoding="utf-8") as f:
        SETTINGS = json.load(f)
except:
    SETTINGS = {}

if platform == "android":
    from jnius import autoclass, cast

class ChatEngine:
    def __init__(self, provider="openai"):
        self.provider = provider
    def ask(self, text):
        return f"[{self.provider}] Echo: {text}"

class YHApp(MDApp):
    def build(self):
        Builder.load_file("app.kv")
        db.init_db()
        self.current_user_id = db.get_default_user_id()
        self.engine = ChatEngine("openai")
        self.plugins = {}
        self.load_plugins()
        self.refresh_plugins()
        return Builder.get_running_app().root

    def load_plugins(self):
        pdir = os.path.join(BASE, "plugins")
        for path in glob.glob(os.path.join(pdir, "*.py")):
            name = os.path.splitext(os.path.basename(path))[0]
            try:
                spec = importlib.util.spec_from_file_location(name, path)
                mod = importlib.util.module_from_spec(spec)
                spec.loader.exec_module(mod)
                if hasattr(mod, "register"):
                    meta = mod.register(self)
                    self.plugins[name] = meta
            except Exception as e:
                print("Plugin load error:", name, e)

    def refresh_plugins(self):
        lst = self.root.ids.plugin_list
        lst.clear_widgets()
        from kivymd.uix.list import OneLineListItem
        for k, v in self.plugins.items():
            lst.add_widget(OneLineListItem(text=f"{v.get('name')} ({k})"))

    def add_reminder_with_location(self, text, lat, lon, radius):
        try:
            latf = float(lat) if lat.strip() else None
            lonf = float(lon) if lon.strip() else None
            rad = int(radius) if radius.strip() else 100
        except:
            Snackbar(text="مقادیر لوکیشن نامعتبر").open(); return
        # parse due time from text (reuse existing parser logic from previous versions if present)
        import utils.reminder_parser as rp
        due_ts = rp.parse_human_time(text)
        if due_ts is None:
            # if user used a location phrase (no time), we set due_ts = now to rely on location trigger
            due_ts = int(time.time())
        rid = db.add_reminder(self.current_user_id, title=text, body="", due_ts=due_ts, repeat="none", tag="", lat=latf, lon=lonf, radius=rad, calendar=0)
        Snackbar(text="یادآور افزوده شد").open()
        self.refresh_plugins()

    def start_location_monitor(self):
        # start the Python service for location monitoring (requires Android and service registration)
        if platform == "android":
            try:
                from jnius import autoclass
                service_name = "org.hooman.yarhooshmand_ai_v9.Locationmonitorsvc"
                service = autoclass(service_name)
                mActivity = autoclass("org.kivy.android.PythonActivity").mActivity
                service.start(mActivity, "")
                Snackbar(text="Location monitor service started").open()
            except Exception as e:
                Snackbar(text=f"خطا: {e}").open()
        else:
            Snackbar(text="Location monitor only available on Android").open()

    # Wake-word control (placeholder)
    def start_wake_word(self):
        wake_word.start_listener(lambda: print("Wake word detected (placeholder)"))

    def stop_wake_word(self):
        wake_word.stop_listener()

if __name__ == "__main__":
    YHApp().run()
