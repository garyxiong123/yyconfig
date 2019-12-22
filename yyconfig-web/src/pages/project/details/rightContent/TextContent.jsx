import React from 'react';
import { UnControlled as CodeMirror } from 'react-codemirror2';
require('codemirror/lib/codemirror.css');
require('codemirror/theme/base16-light.css');
require('codemirror/mode/xml/xml');
require('codemirror/mode/javascript/javascript');
require('codemirror/mode/properties/properties');
require('codemirror/mode/yaml/yaml');
import styles from '../../index.less';
const mode = {
  "json": 'javascript',
  "properties": 'properties',
  "xml": 'xml',
  "yaml": 'yaml'
}
class TextContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: "",
      options: {
        lineNumbers: true,
        mode: 'javascript',
        readOnly: true,
        theme: 'base16-light'
      }
    };
  }
  componentDidMount() {
    const { text } = this.props;
    this.onGetValue();
    this.onEditOption({
      mode: mode[text.format]
    })

  }
  onGetValue = () => {
    const { text } = this.props;
    let value = "", list = text.items || [];
    list.map((vo, i) => {
      let item = vo.item || {};
      if (text.format === 'properties') {
        value += this.onGetProperties(item);
      } else {
        value += this.onGetOther(item);
      }
    })
    this.setState({
      value
    })
  }
  onGetProperties = (text) => {
    let value = "", comment = "";
    // if (value.comment) {
    //   comment = `// ${value.comment} \n`;
    // }
    value = `${text.key} = ${text.value}\n`;
    return value
  }
  onGetOther = (text) => {
    let value = "", comment = "";
    if (value.comment) {
      comment = `// ${value.comment} \n`;
    }
    value = comment + `${text.value}`;
    return value
  }
  onChange = (editor, data, value) => {
    this.setState({
      value
    })
  }
  onEditOption = (data) => {
    const { options } = this.state;
    this.setState({
      options: {
        ...options,
        ...data
      }
    })
  }
  //复制
  onCopy = (ele, id) => {
    // ele.preventDefault()
    let text = document.getElementById(ele);
    try {
      text.select();
      document.execCommand('copy')
      // Toast.info('复制成功')
    } catch (error) {

    }
  }
  render() {
    const { value, options } = this.state;
    return (
      <div>
        <CodeMirror
          value={value}
          options={options}
          onChange={this.onChange}
        />
        {/* 复制*/}
        {/* <input className={styles.copyInput} value={} id='' type='text' readOnly /> */}
      </div>
    );
  }
}
export default TextContent;
