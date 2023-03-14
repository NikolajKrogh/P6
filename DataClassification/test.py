from sklearn.model_selection import StratifiedKFold, cross_validate
from sklearn.ensemble import RandomForestClassifier
import numpy as np

n_samples = 100

# generates 2 n_samples random time series with integer values from 0 to 100.
x1 = np.array([np.random.randint(0, 100, 5) for _ in range(n_samples)])
x2 = np.array([np.random.randint(0, 100, 5) for _ in range(n_samples)])

print(x1)
X = np.hstack((x1, x2))
print(X)