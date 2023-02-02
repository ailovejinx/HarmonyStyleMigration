# f = open('src.txt', encoding='utf-8')
# f1 = open('dest.txt', 'w')
# line = f.readline()
# count = 0
# while line:
#     # 去除空行
#     line = line.strip()
#     # 统计大小写个数
#     for i in line:
#         if i.isupper():
#             count += 1
#     # 大小写转换
#     print(line.lower(), file=f1)
#     line = f.readline()
# print(count, file=f1)

def findprime(n):
    flag = True
    for j in range(2, n):
        if n % j == 0:
            flag = False
            break
    if flag == True:
        return n


def main():
    n = int(input())
    m = int(input())
    prime = []
    for i in range(n, m):
        prime.append(findprime(i))
    print(prime)


if __name__ == "__main__":
    main()
