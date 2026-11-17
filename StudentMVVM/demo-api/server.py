"""API mẫu cho bài thực hành Room & Retrofit. Chạy với Python 3."""

import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from threading import Lock
from uuid import uuid4

HOST = "127.0.0.1"
PORT = 8765
STUDENTS_PATH = "/api/v1/students"
students = [
    {
        "id": "demo-01",
        "full_name": "Trần Minh Anh",
        "student_code": "DEMO001",
        "email": "minhanh@example.com",
        "gpa": 3.65,
    },
    {
        "id": "demo-02",
        "full_name": "Lê Gia Bảo",
        "student_code": "DEMO002",
        "email": "giabao@example.com",
        "gpa": 3.40,
    },
    {
        "id": "demo-03",
        "full_name": "Phạm Ngọc Linh",
        "student_code": "DEMO003",
        "email": "ngoclinh@example.com",
        "gpa": 3.81,
    },
]
students_lock = Lock()


class StudentsHandler(BaseHTTPRequestHandler):
    def send_json(self, status, data):
        body = json.dumps(data, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if self.path != STUDENTS_PATH:
            self.send_json(404, {"error": "Endpoint không tồn tại."})
            return
        with students_lock:
            snapshot = students.copy()
        self.send_json(200, snapshot)

    def do_POST(self):
        if self.path != STUDENTS_PATH:
            self.send_json(404, {"error": "Endpoint không tồn tại."})
            return
        try:
            length = int(self.headers.get("Content-Length", "0"))
            if not 0 < length <= 8192:
                raise ValueError("Invalid content length")
            payload = json.loads(self.rfile.read(length))
            name, code, email, gpa = (
                payload["full_name"], payload["student_code"], payload["email"], payload["gpa"]
            )
            if (
                not all(isinstance(value, str) for value in (name, code, email))
                or not name.strip()
                or not code.strip()
                or type(gpa) not in (int, float)
                or not 0 <= gpa <= 4
            ):
                raise ValueError("Invalid student")
        except (ValueError, KeyError, TypeError):
            self.send_json(400, {"error": "Dữ liệu sinh viên không hợp lệ."})
            return

        student = {
            "id": str(uuid4()),
            "full_name": name.strip(),
            "student_code": code.strip(),
            "email": email.strip(),
            "gpa": gpa,
        }
        with students_lock:
            students.append(student)
        self.send_json(201, student)


if __name__ == "__main__":
    with ThreadingHTTPServer((HOST, PORT), StudentsHandler) as server:
        print(f"API mẫu đang chạy tại http://{HOST}:{PORT}{STUDENTS_PATH}", flush=True)
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            pass
