import React, { Component } from "react";
import DropdownTreeSelect from "react-dropdown-tree-select";
import isEqual from "lodash/isEqual";
import "./GlobitsTreeSelect.scss";
import "react-dropdown-tree-select/dist/styles.css";

export default class GlobitsTreeSelect extends Component {
  constructor(props) {
    super(props);
    this.state = { data: props.data, selectedDatas: [] };
  }

  componentWillReceiveProps = (nextProps) => {
    if (!isEqual(nextProps.data, this.state.data)) {
      this.setState({ data: nextProps.data });
    }
  };

  shouldComponentUpdate = (nextProps) => {
    return !isEqual(nextProps.data, this.state.data);
  };

  uncheckAll = (event) => {
    console.log("uncheck");
    const data = this.state.selectedDatas;
    data[0].checked = false;
    this.setState({ data }, () => {
      console.log(this.state.selectedDatas);
    });
    event.preventDefault();
  };

  onChange = (currentNode, selectedNodes) => {
    this.setState({ selectedDatas: selectedNodes });
    this.props.setFieldValue(this.props.name, currentNode);
  };

  render() {
    const { name, label, data, mode, ...otherProps } = this.props;

    // const configTreeSelect = {
    //   ...otherProps,
    //   id: name,
    //   className: "mdl-demo",
    //   mode: mode ? mode : "radioSelect",
    //   fullWidth: true,
    //   onChange: this.handleChange,
    // };

    return (
      <>
        <DropdownTreeSelect
          id={name}
          texts={{ placeholder: label }}
          className="globits-tree-dropdown"
          mode={mode ? mode : "radioSelect"}
          onChange={this.onChange}
          data={this.state.data}
          {...otherProps}
        />
        <button onClick={this.uncheckAll}>Uncheck all</button>
      </>
    );
    // return <DropdownTreeSelect {...configTreeSelect} />;
  }
}
