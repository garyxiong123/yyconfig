import React from 'react';
import { Spin } from 'antd';
import styles from './index.less';

const Loading = ({}) => (
  <div className={styles.loadingBox}>
    <Spin/>
  </div>
);

export default Loading;