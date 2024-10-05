from functools import lru_cache

from pydantic.v1 import BaseSettings


class Settings(BaseSettings):
    database_url: str = "pos.db"

    class Config:
        env_file = ".env"


@lru_cache()
def get_settings() -> Settings:
    return Settings()
