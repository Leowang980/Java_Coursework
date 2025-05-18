from PIL import Image
import os

def apply_color_tint(input_path, output_path, tint_color=(255, 0, 0), alpha=0.5):
    """
    给图片应用一个颜色滤镜。

    Args:
        input_path (str): 输入图片路径。
        output_path (str): 输出图片路径。
        tint_color (tuple): RGB 格式的颜色元组，如 (255, 0, 0) 为红色。
        alpha (float): 混合的透明度，0.0 (完全透明) 到 1.0 (完全不透明)。
    """
    try:
        # 打开图片
        image = Image.open(input_path)

        # 确保图片是 RGB 模式，以便与颜色滤镜混合
        if image.mode != 'RGB':
            image = image.convert('RGB')

        # 创建一个纯色滤镜图片
        color_filter = Image.new("RGB", image.size, tint_color)

        # 将原图与颜色滤镜混合
        # alpha 参数决定了颜色滤镜的强度
        tinted_image = Image.blend(image, color_filter, alpha)

        # 保存处理后的图片
        # 确保输出目录存在
        output_dir = os.path.dirname(output_path)
        if output_dir and not os.path.exists(output_dir):
            os.makedirs(output_dir)

        tinted_image.save(output_path)
        print(f"成功给 {input_path} 应用颜色滤镜并保存到 {output_path}")

    except Exception as e:
        print(f"处理文件 {input_path} 时发生错误: {e}")

# 示例使用方法 (应用红色滤镜):
input_dir = "sourses"
output_dir_red_tint = "output_red_tint" # 将输出保存到不同目录

# 确保输入目录存在
if not os.path.exists(input_dir):
    print(f"错误：输入目录 '{input_dir}' 不存在。")
else:
    # 确保输出目录存在
    if not os.path.exists(output_dir_red_tint):
        os.makedirs(output_dir_red_tint)

    for file in os.listdir(input_dir):
         # 检查文件扩展名，忽略目录
        if os.path.isfile(os.path.join(input_dir, file)) and file.lower().endswith((".jpg", ".png", ".jpeg", ".bmp", ".gif")):
            input_path = os.path.join(input_dir, file)

            # 构建输出文件名，例如 original.jpg -> original_redtint.jpg
            base_name, ext = os.path.splitext(file)
            output_file_name = f"{base_name}_redtint{ext}"
            output_path = os.path.join(output_dir_red_tint, output_file_name)

            apply_color_tint(input_path, output_path, tint_color=(255, 0, 0), alpha=0.3) # 可以调整 alpha 值