import pandas as pd

!aws s3 ls

!aws s3 cp --recursive s3://cfst-914-1bb539885fdb74aacb1c0093032f07a-s3bucket-10hv34p9hjtdr .

vg = pd.read_csv('data/videogames.csv')
vg.head()
vg.dropna().sort_values('Critic_Score', ascending=False)

