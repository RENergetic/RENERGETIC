const keys = ["dashboard"];

class Storage {
  constructor() {
    this.data = {};
    this.load();
  }
  clear() {
    for (const k of keys) {
      sessionStorage.setItem(k, null);
    }
  }
  load() {
    for (const k of keys) {
      var j = sessionStorage.getItem(k);
      var o = JSON.parse(j);
      this.data[k] = o;
    }
  }
  async update(k, o) {
    this.data[k] = o;
    sessionStorage.setItem(k, JSON.stringify(o));
  }

  async push(k, o) {
    let l = await this.get(k, []);
    l.push(o);
    storage.update(k, l);
  }

  async updateList(k, o) {
    let idx = this.data[k].findIndex((el) => el.id === o.id);
    this.data[k][idx] = o;
    // sessionStorage.setItem(this.data, JSON.stringify(o));
  }

  async setDefault(k, defaultValue = null, force = false) {
    if (this.data[k] == null || force) {
      this.update(k, defaultValue);
    }
  }

  async get(k, defaultValue = null) {
    let d = this.data[k];
    return d == null
      ? (async () => {
          return defaultValue;
        })()
      : d;
  }
}
var storage = new Storage();
export default storage;
