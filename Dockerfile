FROM alpine:3.5
RUN apk add --update py2-pip
COPY requirements.txt /usr/src/app/
RUN pip3 install --no-cache-dir -r /usr/src/app/requirements.txt
COPY web.py /usr/src/app/
COPY templates/index.html /usr/src/app/templates/
COPY static/css/style.css /usr/src/app/static/css/
COPY static/img/platonic-icosahedron.gif /usr/src/app/static/img/
COPY static/img/platonic-octahedron.gif /usr/src/app/static/img/
COPY static/img/platonic-pyramid.gif /usr/src/app/static/img/
COPY static/js/footer-functions.js /usr/src/app/static/js/
COPY static/js/header-functions.js /usr/src/app/static/js/
COPY static/js/utils.js /usr/src/app/static/js/
EXPOSE 5000
CMD ["python3", "/usr/src/app/web.py"]
