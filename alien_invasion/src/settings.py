class Settings:
    def __init__(self):
        # 屏幕尺寸
        self.screen_width = 1200
        self.screen_height = 800
        # 背景色
        self.bg_color = (230, 230, 230)
        # self.bg_color = (255, 255, 255)
        # 飞船设置
        # self.ship_speed_factor = 1.5
        self.ship_limit = 3
        # 子弹设置
        # self.bullet_speed_factor = 3
        self.bullet_width = 3
        self.bullet_height = 15
        self.bullet_color = (60, 60, 60)
        self.bullets_allowed = 3
        # 外星人设置
        self.fleet_drop_speed = 1
        self.speedup_scale = 1.1
        self.score_scale = 1.5
        self.initialize_dynamic_settings()

    def initialize_dynamic_settings(self):
        self.ship_speed_factor = 1.5
        self.bullet_speed_factor = 3
        self.alien_speed_factor = 1
        self.fleet_direction = 1
        self.alien_points = 50

    def increase_speed(self):
        self.ship_speed_factor *= self.speedup_scale
        self.bullet_speed_factor *= self.speedup_scale
        self.alien_speed_factor *= self.speedup_scale
        self.alien_alien_ponits = int(self.alien_points * self.score_scale)