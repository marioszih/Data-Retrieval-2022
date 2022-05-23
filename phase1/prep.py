import numpy as np
import pandas as pd

f = pd.read_csv("rotten_tomatoes_movies.csv")

f = f.dropna()
f = f[["rotten_tomatoes_link","movie_title","movie_info","critics_consensus","rating","genre","directors","writers","cast","runtime_in_minutes","tomatometer_rating","audience_rating"]]

print(f)

f.to_csv("edited_file.csv", index=False)

first_20 = f.head(20)
first_20.to_csv("first_20.csv", index=False)