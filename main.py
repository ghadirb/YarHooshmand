from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.core.window import Window

try:
    from plyer import notification
except Exception:
    notification = None

class Root(BoxLayout):
    pass

class YarApp(App):
    def build(self):
        Window.size = (360, 640)
        root = BoxLayout(orientation="vertical", padding=16, spacing=12)
        lbl = Label(text="یار هوشمند - نسخه پایه", font_size="18sp")
        btn = Button(text="اعلان تست", size_hint=(1, None), height=48)
        btn.bind(on_release=self.send_notif)
        root.add_widget(lbl)
        root.add_widget(btn)
        return root

    def send_notif(self, *_):
        if notification:
            notification.notify(title="یار هوشمند", message="اعلان تست موفق بود ✅")
        else:
            print("plyer.notification در دسترس نیست، ولی برنامه کار می‌کند.")

if __name__ == "__main__":
    YarApp().run()
