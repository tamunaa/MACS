from __future__ import annotations

import enum
import time
from dataclasses import dataclass, field
from typing import Callable, Dict, Protocol, TypeVar


class AlertType(enum.Enum):
    TEMPERATURE = "temperature"
    HUMIDITY = "humidity"
    WIND_SPEED = "wind_speed"
    GENERAL = "general"


class Observer(Protocol):
    def update(self, weather_data: WeatherData) -> None:
        ...


ObserverFactory = Callable[[], Observer]

T = TypeVar('T')


@dataclass(frozen=True)
class WeatherData:
    temperature: float
    humidity: float
    wind_speed: float
    timestamp: float = field(default_factory=time.time)

    def __post_init__(self) -> None:
        for field_name in ['temperature', 'humidity', 'wind_speed']:
            value = getattr(self, field_name)
            if not isinstance(value, (int, float)):
                raise TypeError(f"{field_name} must be a number")

    def to_dict(self) -> Dict[str, float]:
        return {
            "temperature": self.temperature,
            "humidity": self.humidity,
            "wind_speed": self.wind_speed,
            "timestamp": self.timestamp
        }
