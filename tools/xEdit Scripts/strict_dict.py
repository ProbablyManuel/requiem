class strict_dict(dict):
    def __setitem__(self, key, value):
        if dict.__contains__(self, key):
            raise ValueError("Key already exists")
        dict.__setitem__(self, key, value)
