import socket
 
HOST = "localhost"
PORT = 5000
 
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))
 
sock.sendall("10\n".encode("utf-8"))