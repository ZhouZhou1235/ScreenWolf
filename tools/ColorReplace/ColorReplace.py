# 图片颜色替换

from PIL import Image
import numpy as np
import os


# 扫描文件夹中的文件
def scan_files(directory:str,extensions=('.png','.jpg','.jpeg')):
    file_list = []
    for root, _, files in os.walk(directory):
        for file in files:
            if file.lower().endswith(extensions):
                file_list.append(os.path.join(root, file))
    return file_list

# 递归扫描所有文件
def scan_files_recursive(directory:str,get_dirs=False):
    result = []
    if not os.path.isdir(directory): return result
    for root,dirs,files in os.walk(directory):
        for file in files:
            result.append(os.path.join(root,file))
        if get_dirs:
            for dir_name in dirs:
                result.append(os.path.join(root,dir_name))
    return result

# 图片替换颜色
def replace_color(
        image_path='',
        output_path='',
        target_color=(),
        replacement_color=(),
        tolerance=0
    ):
    img = Image.open(image_path).convert("RGBA")
    img_array = np.array(img)
    if len(target_color) == 3:
        target_color = target_color + (255,)
    if len(replacement_color) == 3:
        replacement_color = replacement_color + (255,)
    color_diff = np.abs(img_array - np.array(target_color))
    color_distance = np.sqrt(np.sum(color_diff[:,:,:3]**2, axis=2))
    if tolerance == 0:
        mask = np.all(img_array == target_color, axis=2)
    else:
        mask = color_distance <= tolerance
    result_array = img_array.copy()
    result_array[mask] = replacement_color
    result_img = Image.fromarray(result_array)
    result_img.save(output_path)

# 图片替换颜色 考虑透明通道
# 透明度大于alpha_threshold的像素才会被替换
def replace_color_with_alpha(
        image_path='',
        output_path='',
        target_color=(),
        replacement_color=(),
        alpha_threshold=128,
        tolerance=0
    ):
    img = Image.open(image_path).convert("RGBA")
    img_array = np.array(img)
    if len(target_color) == 3:
        target_color = target_color + (255,)
    if len(replacement_color) == 3:
        replacement_color = replacement_color + (255,)
    color_diff = np.abs(img_array - np.array(target_color))
    color_distance = np.sqrt(np.sum(color_diff[:,:,:3]**2, axis=2))
    if tolerance == 0:
        color_mask = np.all(img_array[:,:,:3] == target_color[:3], axis=2)
    else:
        color_mask = color_distance <= tolerance
    alpha_mask = img_array[:,:,3] >= alpha_threshold
    mask = color_mask & alpha_mask
    result_array = img_array.copy()
    result_array[mask] = replacement_color
    result_img = Image.fromarray(result_array)
    result_img.save(output_path)

# 批量替换多张图片的颜色
def batch_replace_color(
        image_paths=[],
        output_dir='',
        target_color=(),
        replacement_color=(),
        tolerance=0
    ):
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    for image_path in image_paths:
        if os.path.isfile(image_path):
            filename = os.path.basename(image_path)
            output_path = os.path.join(output_dir, f"replaced_{filename}")
            replace_color(image_path, output_path, target_color, replacement_color, tolerance)

# 批量替换多张图片的颜色 覆盖原文件
def batch_replace_color_cover(
        image_paths=[],
        target_color=(),
        replacement_color=(),
        tolerance=0
    ):
    for image_path in image_paths:
        if os.path.isfile(image_path):
            output_path = image_path
            replace_color(image_path, output_path, target_color, replacement_color, tolerance)


if __name__ == "__main__":
    print("图片颜色替换")
    image_dir = input("资源路径：")
    color_replace_list = []
    while True:
        target_color_input = input("目标颜色 (R,G,B) 直接回车结束：")
        if target_color_input == "": break
        replacement_color_input = input("替换颜色 (R,G,B)：")
        tolerance_input = input("容差 (0-255) 默认0：")
        try:
            target_color = tuple(map(int, target_color_input.split(',')))
            replacement_color = tuple(map(int, replacement_color_input.split(',')))
            tolerance = int(tolerance_input) if tolerance_input else 0
            color_replace_list.append((target_color, replacement_color, tolerance))
        except:
            print("输入格式错误")
    image_files = scan_files_recursive(image_dir)
    for target_color, replacement_color, tolerance in color_replace_list:
        batch_replace_color_cover(
            image_paths=image_files,
            target_color=target_color,
            replacement_color=replacement_color,
            tolerance=tolerance
        )
    print("完成")
