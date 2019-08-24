FROM python:3.7-alpine
ADD . /data
WORKDIR /data
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple && python dandelion.py init
ENTRYPOINT ["python", "dandelion.py", "runserver", "--host", "0.0.0.0"]