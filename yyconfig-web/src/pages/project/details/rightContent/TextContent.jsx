import React, { Fragment } from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
require('codemirror/lib/codemirror.css');
require('codemirror/theme/base16-light.css');
require('codemirror/theme/eclipse.css');
require('codemirror/mode/xml/xml');
require('codemirror/mode/javascript/javascript');
require('codemirror/mode/properties/properties');
require('codemirror/mode/yaml/yaml');
import styles from '../../index.less';
import { Button, message, Row, Col } from 'antd';
import { project } from '@/services/project';
const mode = {
  "JSON": 'javascript',
  "Properties": 'properties',
  "XML": 'xml',
  "YAML": 'yaml'
}
class TextContent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      value: "",
      oldValue: "",
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
      if (text.format === 'Properties') {
        value += this.onGetProperties(item, i, list.length);
      } else {
        value += this.onGetOther(item);
      }
    })
    this.setState({
      value
    })
  }
  onGetProperties = (text, i, len) => {
    let value = "";
    if (i === len - 1) {
      value = `${text.key} = ${text.value}`;
    } else {
      value = `${text.key} = ${text.value}\n`;
    }
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
  onEdit = () => {
    const { options, value } = this.state;
    this.setState({
      oldValue: value
    })
    this.onEditOption({ readOnly: false, theme: 'eclipse' })
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
  onCopy = (ele, e) => {
    e && e.preventDefault();
    let text = document.getElementById(ele);
    try {
      text.select();
      document.execCommand('copy')
      message.success('复制成功');
    } catch (error) {

    }
  }
  onSaveText = async () => {
    const { value } = this.state;
    const { text, onSuccess } = this.props;
    let baseInfo = text.baseInfo || {};
    let res = await project.modifyItemsByTexts({
      appEnvClusterNamespaceId: baseInfo.id,
      configText: value,
      format: text.format
    });
    if (res && res.code === '1') {
      onSuccess();
      this.onEditOption({
        readOnly: true,
        theme: 'base16-light'
      })
    }
  }
  onCanCel = () => {
    const { oldValue } = this.state;
    this.setState({
      value: oldValue,
      // oldValue: ''
    })
    this.onEditOption({ readOnly: true, theme: 'base16-light' })
  }
  renderOpa() {
    const { text } = this.props;
    const { options } = this.state;
    let baseInfo = text.baseInfo || {};
    return (
      <div>
        {
          options.readOnly ?
            <Row type="flex" justify="end" gutter={16} style={{ marginBottom: 15 }}>
              <Col>
                <Button onClick={(e) => this.onCopy(baseInfo.id, e)} size="small">复制</Button>
              </Col>
              <Col>
                <Button size="small" type="primary" onClick={() => { this.onEdit() }}>编辑</Button>
              </Col>
            </Row> :
            <Row type="flex" justify="end" gutter={16} style={{ marginBottom: 15 }}>
              <Col>
                <Button onClick={this.onCanCel} size="small">取消</Button>
              </Col>
              <Col>
                <Button size="small" type="primary" onClick={() => { this.onSaveText() }}>确定</Button>
              </Col>
            </Row>
        }
      </div>
    )
  }
  render() {
    const { value, options } = this.state;
    const { text } = this.props;
    let baseInfo = text.baseInfo || {};
    return (
      <div>
        {
          this.renderOpa()
        }
        <CodeMirror
          value={value}
          options={options}
          onBeforeChange={this.onChange}
        />
        {/* 复制*/}
        <textarea className={styles.copyInput} value={value} id={baseInfo.id} type='text' readOnly />
      </div>
    );
  }
}
export default TextContent;
