# Introduction

本项目基于CS架构，利用Java语言，在鸿蒙开发工具DevEco Studio中开发了一款能够进行图片风格样式迁移的程序，实现对图片添加山水画风格滤镜和梵高风格滤镜的功能。

<div align="center"><img src="./img/img7.png" style="zoom:50%;margin-bottom:10px;" /></div>

# Demo

实机展示视频：https://www.bilibili.com/video/BV1ER4y1P7qT

教程：https://ailovejinx.github.io/jekyll/2022-05-22-style_mig.html

# Structure

```
HarmonyStyleMigration
└─── client: HarmonyOS software(front end)
        └─── entry: Source code entry
└─── server: Flask server(back end)

```

# Acknowledgement

Thanks to [Jun-Yan Zhu](https://www.cs.cmu.edu/~junyanz/), I implemented this project by replicating [CycleGAN](https://github.com/junyanz/CycleGAN). If it helps you in your research, please cite Please cite their work.

```
@inproceedings{CycleGAN2017,
  title={Unpaired Image-to-Image Translation using Cycle-Consistent Adversarial Networks},
  author={Zhu, Jun-Yan and Park, Taesung and Isola, Phillip and Efros, Alexei A},
  booktitle={Computer Vision (ICCV), 2017 IEEE International Conference on},
  year={2017}
}


@inproceedings{isola2017image,
  title={Image-to-Image Translation with Conditional Adversarial Networks},
  author={Isola, Phillip and Zhu, Jun-Yan and Zhou, Tinghui and Efros, Alexei A},
  booktitle={Computer Vision and Pattern Recognition (CVPR), 2017 IEEE Conference on},
  year={2017}
}
```