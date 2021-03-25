const YamlValidator = require('yaml-validator');

const files = [
  '../../bitrise.yml'
];

const validator = new YamlValidator();
validator.validate(files);
const report = validator.report();

if (report === 0) {
  console.log("yml is valid 🙌")
} else {
  console.log(report)
}
