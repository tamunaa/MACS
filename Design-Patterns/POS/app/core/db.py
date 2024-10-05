import sqlite3
from typing import Callable

from .config import get_settings


def init_db() -> None:
    """Initialize the database with required tables"""
    settings = get_settings()
    with sqlite3.connect(settings.database_url) as conn:
        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS units (
                id TEXT PRIMARY KEY,
                name TEXT UNIQUE NOT NULL
            )
        """
        )

        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS products (
                id TEXT PRIMARY KEY,
                unit_id TEXT NOT NULL,
                name TEXT NOT NULL,
                barcode TEXT UNIQUE NOT NULL,
                price INTEGER NOT NULL,
                FOREIGN KEY (unit_id) REFERENCES units (id)
            )
        """
        )

        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS receipts (
                id TEXT PRIMARY KEY,
                status TEXT NOT NULL CHECK (status IN ('open', 'closed')),
                total INTEGER NOT NULL DEFAULT 0
            )
        """
        )

        conn.execute(
            """
            CREATE TABLE IF NOT EXISTS receipt_products (
                id TEXT PRIMARY KEY,
                receipt_id TEXT NOT NULL,
                product_id TEXT NOT NULL,
                quantity REAL NOT NULL,
                price INTEGER NOT NULL,
                total INTEGER NOT NULL,
                FOREIGN KEY (receipt_id) REFERENCES receipts (id),
                FOREIGN KEY (product_id) REFERENCES products (id)
            )
        """
        )

        conn.commit()


def get_db_connection() -> Callable[[], sqlite3.Connection]:
    database_url = get_settings().database_url

    def create_connection() -> sqlite3.Connection:
        conn = sqlite3.connect(database_url, check_same_thread=False)
        conn.row_factory = sqlite3.Row
        return conn

    return create_connection
