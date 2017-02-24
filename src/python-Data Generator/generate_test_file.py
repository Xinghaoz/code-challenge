import random, os, sys

# Generate random test data.  I think there is no need to comment here :)
class Generator:
    names = ["Alice", "Bob", "Charlie", "David", "Elise", "Frank", "Harry"]
    cities = ["Mountain View", "Palo Alto", "Pittsburgh", "New York", "Los Angels", "San Francisco", "Seattle"]
    states = ["CA", "CA", "PA", "NY", "CA", "CA", "WA"]
    verbs = ["NEW", "UPDATE", "UPLOAD"]
    types = ["CUSTOMER", "SITE_VISIT", "IMAGE", "ORDER"]

    def __init__(self):
        self.customerSet = set()

    def generate_random_event(self):
        customer_id = random.randrange(1,25)
        key = ''

        yy = random.randrange(2011, 2016)
        MM = "%02d" % random.randrange(1,13)
        dd = "%02d" % random.randrange(1,29)
        hh = "%02d" % random.randrange(1,25)
        mm = "%02d" % random.randrange(1,61)
        ss = "%02d" % random.randrange(1,61)
        SSS = "%02d" % random.randrange(1,1001)

        eventType = self.types[random.randrange(0, 4)]
        verb = "NEW"
        for _ in range(0, 12):
            key = key + random.choice('abcdef0123456789')

        if eventType == "CUSTOMER":
            last_name = self.names[random.randrange(0, 7)]
            random_index = random.randrange(0, 7)
            adr_city = self.cities[random_index]
            adr_state = self.states[random_index]

            if customer_id in self.customerSet:
                verb = "UPDATE"
            result = '{{"type": "{}", "verb": "{}", "key": "{}", "event_time": "{}-{}-{}T{}:{}:{}.{}Z", "last_name": "{}", "adr_city": "{}", "adr_state": "{}"}},'.format(
                        eventType, verb, customer_id, yy, MM, dd, hh, mm, ss, SSS, last_name, adr_city, adr_state)
            return result

        elif eventType == "SITE_VISIT":
            result = '{{"type": "{}", "verb": "{}", "key": "{}", "event_time": "{}-{}-{}T{}:{}:{}.{}Z", "customer_id": "{}", "tags": {{"some key": "some value"}}}},'.format(
                        eventType, verb, key, yy, MM, dd, hh, mm, ss, SSS, customer_id
            )
            return result
        elif eventType == "IMAGE":
            result = '{{"type": "{}", "verb": "{}", "key": "{}", "event_time": "{}-{}-{}T{}:{}:{}.{}Z", "customer_id": "{}", "camera_make": "Canon", "camera_model": "EOS 80D"}},'.format(
                        eventType, verb, key, yy, MM, dd, hh, mm, ss, SSS, customer_id
            )
            return result
        elif eventType == "ORDER":
            random_amount = "%.2f" % random.uniform(1, 43.96)
            result = '{{"type": "{}", "verb": "{}", "key": "{}", "event_time": "{}-{}-{}T{}:{}:{}.{}Z", "customer_id": "{}", "total_amount": "{} USD"}},'.format(
                        eventType, verb, key, yy, MM, dd, hh, mm, ss, SSS, customer_id, random_amount
            )
            return result

def generate(num):
    g = Generator()
    path = os.path.dirname(os.path.dirname(os.path.abspath(os.path.dirname(sys.argv[0])))) + '/input/'

    with open(path + 'random{}.txt'.format(num), 'w') as f:
        events = '[\n'
        for _ in range(0, num):
            events = events + g.generate_random_event() + ',\n'
        events = events + ']'
        f.write(events)

if __name__ == "__main__":
    generate(50)
    generate(500)
    generate(2500)
    generate(10000)
