import React, { useState } from "react";
import { observer } from "mobx-react";
import {
  Button,
  Grid,
  makeStyles,
  Card,
  CardContent,
  CardActions,
  FormControl,
  Input,
  InputAdornment,
} from "@material-ui/core";
import { useStore } from "../../stores";
import PropTypes from "prop-types";
import ConstantList from "../../appConfig";
import { useHistory, useParams } from "react-router-dom";
import SearchIcon from "@material-ui/icons/Search";
import { alpha, withStyles } from "@material-ui/core/styles";
import TreeItem from "@material-ui/lab/TreeItem";
import { useSpring, animated } from "react-spring/web.cjs";
import Collapse from "@material-ui/core/Collapse";
import TreeView from "@material-ui/lab/TreeView";
import "./time-sheet-styles.scss";

const useStyles = makeStyles((theme) => ({
  root: {
    top: "10px",
  },
  cardRoot: {
    marginBottom: "20px",
    borderRadius: "0",
  },
  header: {
    padding: "12px!important",
    textAlign: "center",
    color: "#fff",
    "& p": {
      margin: 0,
    },
  },
  innerHeader: {
    background: "#01c0c8",
  },
  body: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    // [theme.breakpoints.down("xs")]: {
    // maxHeight: "300px",
    // overflowY: "auto",
    // },
  },
  containerComp: {
    [theme.breakpoints.down("xs")]: {
      maxHeight: "300px",
      overflowY: "auto",
    },
  },
  containerproject: {
    maxHeight: "450px",
    overflowY: "auto",
    textAlign:"left"
    // scrollbarWidth: "none"
  },
  formSearch: {},
  allStyle:{
    padding:"0% 35% 0% 40%",
    justifyContent:"center"
  }
}));

function TransitionComponent(props) {
  const style = useSpring({
    from: { opacity: 0, transform: "translate3d(20px,0,0)" },
    to: {
      opacity: props.in ? 1 : 0,
      transform: `translate3d(${props.in ? 0 : 20}px,0,0)`,
    },
  });

  return (
    <animated.div style={style}>
      <Collapse {...props} />
    </animated.div>
  );
}

TransitionComponent.propTypes = {
  /**
   * Show the component; triggers the enter or exit states
   */
  in: PropTypes.bool,
};

const StyledItem = withStyles((theme) => ({
  iconContainer: {
    "& .close": {
      opacity: 0.3,
    },
  },
  group: {
    marginLeft: 7,
    paddingLeft: 18,
    borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
    alignContent:"left",
  },
}))((props) => (
  <TreeItem {...props} TransitionComponent={TransitionComponent} />
));

export default observer(function ListProject(props) {
  const params = useParams();
  const history = useHistory();
  const classes = useStyles();
  const { timeSheetDetailsStore } = useStore();
  const [all, setAll] = useState(false);
  const [test, setTest] = useState("");
  const { handleSelectProject, selectedProject } =
    timeSheetDetailsStore;
  // const { values, setFieldValue } = useFormikContext();
  const renderTree = (nodes) =>
    nodes.id ? (
      <StyledItem
        key={nodes.id}
        nodeId={nodes.id}
        label={nodes.name}
        onLabelClick={(event) => {
          event.preventDefault();
        }}
      >
        {Array.isArray(nodes.children)
          ? nodes.children.map((node) => renderTree(node))
          : null}
      </StyledItem>
    ) : null;

  return (
    <div className={classes.root}>
      <Card className={classes.cardRoot}>
        <div className={classes.innerHeader}>
          <CardContent className={classes.header}>
            <p>Danh sách dự án:</p>
          </CardContent>
        </div>
        <CardActions className={classes.body}>
          <Grid container className={classes.containerComp}>
            <Grid item xs={12} >
              <Button
                size="small"
                color="secondary"
                className={`project-item ${
                  params?.id || selectedProject?.id ? "" : "active"
                }` }
                onClick={() => {
                  setAll(true);
                  handleSelectProject();
                  history.push(
                    ConstantList.ROOT_PATH + "timesheetDetails/list"
                  );
                  setTest("");
                  props.handleSearch("");
                }}
              >
                <div className={classes.allStyle}>
                Tất cả
                </div>
              </Button>
            </Grid>
            
            <FormControl className={classes.formSearch} fullWidth>
              <Input
                className="search_box"
                // ref={test}
                onChange={(e) => {
                  props.handleSearch(e.target.value);
                  setTest(e.target.value);
                }}
                value={test}
                placeholder="Nhập từ khóa"
                id="search_box_project"
                startAdornment={
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                }
              />
            </FormControl>
            <Grid container className={classes.containerproject}>
              {props.projectTreeview?.map((item, index) => {
                return (
                  <Grid key={index} item xs={12}>
                    <TreeView
                      key={index}
                      className={classes.root}
                      defaultExpanded={props.defaultExpanded}
                      expanded={props.expanded}
                      defaultCollapseIcon={props.defaultCollapseIcon}
                      defaultExpandIcon={props.defaultExpandIcon}
                      // defaultEndIcon={<CloseSquare />}
                      onNodeSelect={props.onNodeSelect}
                    >
                      <Button
                        size="small"
                        color="secondary"
                        className={`project-item ${
                          params?.id
                            ? item?.id === params?.id
                              ? "active"
                              : ""
                            : item?.id === selectedProject?.id
                            ? "active"
                            : ""
                        }`}
                        onClick={() => {
                          handleSelectProject(item);
                          history.push(
                            `${ConstantList.ROOT_PATH}timesheetDetails/list/${item?.id}`
                          );
                        }}
                      >
                        {/* {item.name} */}
                        {renderTree(item)}
                      </Button>
                    </TreeView>
                  </Grid>
                );
              })}
            </Grid>
          </Grid>
        </CardActions>
      </Card>
    </div>
  );
});
