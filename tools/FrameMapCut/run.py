# 精灵图裁切工具

import os
from PIL import Image

def split_sprite_sheet(
    image_path: str,
    output_dir: str,
    rows: int,
    cols: int,
    start_index=0
):
    try:
        sprite_sheet = Image.open(image_path)
        sheet_width, sheet_height = sprite_sheet.size
        frame_width = sheet_width // cols
        frame_height = sheet_height // rows
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        count = start_index
        for row in range(rows):
            for col in range(cols):
                left = col * frame_width
                upper = row * frame_height
                right = left + frame_width
                lower = upper + frame_height
                frame = sprite_sheet.crop((left, upper, right, lower))
                if not is_blank_frame(frame):
                    frame_name = f"{row}-{col}.png"
                    frame_path = os.path.join(output_dir,frame_name)
                    frame.save(frame_path)
                    count += 1                    
        return True
    except Exception as e:
        print(e)
        return False

def is_blank_frame(frame):
    if frame.mode != 'RGBA':
        frame = frame.convert('RGBA')
    alpha = frame.getchannel('A')
    alpha_pixels = list(alpha.getdata())
    for pixel in alpha_pixels:
        if pixel > 0:
            return False
    return True

def run():
    frame_maps_dir = os.path.join("FrameMaps")
    image_files = []
    if os.path.exists(frame_maps_dir):
        for file in os.listdir(frame_maps_dir):
            if file.lower().endswith(('.png','.jpg','.jpeg','.bmp','.gif')):
                image_files.append(file)
    if image_files:
        for i, file in enumerate(image_files, 1):
            print(f"{i}. {file}")
        try:
            choice = int(input("选择要处理的精灵图 输入序号："))-1
            if 0 <= choice < len(image_files):
                image_path = os.path.join(frame_maps_dir, image_files[choice])
            else:
                print("无效选择 使用第一个精灵图")
                image_path = os.path.join(frame_maps_dir, image_files[0])
        except ValueError:
            print("无效输入 使用第一个精灵图")
            image_path = os.path.join(frame_maps_dir, image_files[0])
    else:
        image_path = input("精灵图路径: ").strip('"')
    if not os.path.isfile(image_path):
        return
    output_dir = os.path.join("frames")
    try:
        rows = int(input("行数: "))
        cols = int(input("列数: "))
    except ValueError:
        return
    start_index_input = input("起始索引 默认0：")
    start_index = 0
    if start_index_input:
        try:
            start_index = int(start_index_input)
        except ValueError:
            start_index = 0
    success = split_sprite_sheet(
        image_path, output_dir, rows, cols, start_index
    )
    if success:
        print(f"\n完成 帧已保存到 {output_dir}")
    else:
        print("\n失败")

if __name__ == '__main__':
    print('=== FrameMapCut 精灵图裁切工具 ===')
    while True:
        print('1 裁切')
        print('0 退出')
        option = int(input('option:'))
        if option==0:
            break
        elif option==1:
            run()
