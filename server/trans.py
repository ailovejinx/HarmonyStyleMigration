# import torch
# import torchvision
# from unet import UNet
# model = UNet(3, 2)
# # model = torch.load("./model.pth", map_location=torch.device('cpu'))
# model = torch.load('LandscapeModel.pth', map_location=torch.device('cpu'))
# model.eval()
# example = torch.rand(1, 3, 256, 256)
# traced_script_module = torch.jit.trace(model, example)
# traced_script_module.save("LandscapeModel.pt")

import torch
import torchvision
from torch.utils.mobile_optimizer import optimize_for_mobile
from unet import UNet
model = UNet(3, 2)

# model = torchvision.models.mobilenet_v2(pretrained=True)
model = torch.load('LandscapeModel.pth', map_location=torch.device('cpu'))
# model.load_state_dict(torch.load('LandscapeModel.pth'))
model.eval()
example = torch.rand(1, 3, 512, 512)
traced_script_module = torch.jit.trace(model, example)
traced_script_module_optimized = optimize_for_mobile(traced_script_module)
traced_script_module_optimized._save_for_lite_interpreter("LandscapeModel.ptl")
