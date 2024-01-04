from pydub import AudioSegment
import optparse
import os
import re

'''
功能：无损音乐集合切分-根据.cue文件切分音频
环境：python3.6+pydub（镜像源：https://pypi.tuna.tsinghua.edu.cn/simple）
运行脚本：python splitter.py -d F://music
creat by Mr.shi at 2024/1/4
'''


# 读取音频和切片文件
def read_files(path):
    wav, cue = None, None
    for file in os.listdir(path):
        if file.endswith('.wav'):
            wav = path + '/' + file
        elif file.endswith('.cue'):
            cue = path + '/' + file

    if wav is None or cue is None:
        print("### 文件缺失 ###")
        return

    return wav, cue


# 处理切片文件
def handle_cue(cue):
    records, index = [0] * 20, 0
    with open(cue, 'r') as f:
        for item in f:
            if item.__contains__("TRACK"):
                records[index] = {}
            elif records[index] != 0:
                if item.__contains__("TITLE"):
                    pattern = re.compile(r'\"([^\"]*)\"')
                    name = pattern.findall(item)[0].strip()
                    records[index]['name'] = name.replace('  [mp3bst.com]', '')
                elif item.__contains__("INDEX 01"):
                    pattern = re.compile(r'([0-5]\d):([0-5]\d):(\d+)?')
                    s, f, m = pattern.findall(item)[0]  # 分秒毫秒
                    records[index]['index'] = (int(s) * 60 + int(f)) * 1000 + int(m)
                    index = index + 1

    return records[:index]


# 分割音频。单位：毫秒
def split_wave(file, records):
    wav = AudioSegment.from_wav(file)  # 读取音频文件
    for i in range(len(records)):
        record = records[i]  # 切片信息

        if record == 0:
            break

        start = record['index'] + 500  # 切片开始时间
        fileName = record['name']  # 切片名称
        if i + 1 >= len(records) or records[i + 1] == 0:
            wav[start:].export(fileName + '.wav', format="wav")  # 导出文件
        else:
            end = records[i + 1]['index'] - 500  # 下张切片的开始时间前0.5秒
            wav[start: end].export(fileName + '.wav', format="wav")  # 导出文件


# 主方法
def main():
    # 定义脚本运行所需参数
    parser = optparse.OptionParser('usage % proge -d <base dir>')
    parser.add_option('-d', dest='dir', type='string', help='添加执行目录')
    (options, args) = parser.parse_args()

    if options.dir is None or options.dir == '':
        print("### 执行目录为空 ###")
        return

    wavFile, cueFile = read_files(options.dir)
    print("### 文件读取结束 ###")
    parseCue = handle_cue(cueFile)
    print("### 文件解析结束 ###")
    split_wave(wavFile, parseCue)
    print("### 切片结束 ###")


if __name__ == '__main__':
    main()
