from flask import Flask, request, make_response, render_template
from PIL import Image
from io import BytesIO
from test import Test
import os
os.environ["KMP_DUPLICATE_LIB_OK"]="TRUE"



app = Flask(__name__)
basedir = os.path.abspath(os.path.dirname(__file__))
'''因为是文件，所以只能是POST方式'''


@app.route("/upload/landscape", methods=["POST"])
def uploadLandscape():
    """接受前端传送来的文件"""
    print("获取上传文件信息")

    print(request.method)

    try:
        tempcache = request.get_data()
        print(tempcache[0:100])
        bytes_stream = BytesIO(tempcache)
        img = Image.open(bytes_stream)
        # 初始化一个空字节流
        imgByteArr = BytesIO()
        # 将图片以'PNG'保存到空字节流
        img.save(imgByteArr, format('PNG'))
        # 无视指针，获取全部内容，类型由io流变成bytes
        imgByteArr = imgByteArr.getvalue()
        img_name = 'test.png'
        with open(os.path.join('pic', img_name), 'wb') as f:
            f.write(imgByteArr)
        
        # 调用函数进行迁移
        Test('LandscapeWeights')

        return "样式迁移成功！"
    except Exception as e:
        print(e)
        return "样式迁移失败！"

@app.route("/upload/vangogh", methods=["POST"])
def uploadVangogh():
    """接受前端传送来的文件"""
    print("获取上传文件信息")

    print(request.method)

    try:
        tempcache = request.get_data()
        print(tempcache[0:100])
        bytes_stream = BytesIO(tempcache)
        img = Image.open(bytes_stream)
        # 初始化一个空字节流
        imgByteArr = BytesIO()
        # 将图片以'PNG'保存到空字节流
        img.save(imgByteArr, format('PNG'))
        # 无视指针，获取全部内容，类型由io流变成bytes
        imgByteArr = imgByteArr.getvalue()
        img_name = 'test.png'
        with open(os.path.join('pic', img_name), 'wb') as f:
            f.write(imgByteArr)
        
        # 调用函数进行迁移
        Test('VangoghWeights')

        return "样式迁移成功！"
    except Exception as e:
        print(e)
        return "样式迁移失败！"

@app.route('/<string:modelname>/<string:filename>', methods=['GET'])
def show_photo(filename, modelname):
    file_dir = os.path.join(basedir, 'results/'+modelname+'/test_latest/images')
    if request.method == 'GET':
        if filename is None:
            pass
        else:
            # print(basedir)
            image_data = open(os.path.join(file_dir, '%s' % filename), "rb").read()
            response = make_response(image_data)
            response.headers['Content-Type'] = 'image/png'
            return response
    else:
        pass
@app.route('/version')
def index():
    user = { 'nickname': 'Miguel' } # 定义变量
    return render_template("index.html")


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5050)
