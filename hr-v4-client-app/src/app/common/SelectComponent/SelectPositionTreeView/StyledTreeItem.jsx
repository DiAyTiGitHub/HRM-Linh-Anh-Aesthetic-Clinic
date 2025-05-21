import Collapse from "@material-ui/core/Collapse";
import { alpha, withStyles } from "@material-ui/core/styles";
import TreeItem from "@material-ui/lab/TreeItem";
import React from "react";
import { animated, useSpring } from "react-spring/web.cjs";

const PAGE_SIZE = 20;

// Styled TreeItem
const StyledTreeItem = withStyles((theme) => ({
    iconContainer: {
        "& .close": {
            opacity: 0.3,
        },
    },
    group: {
        marginLeft: 7,
        paddingLeft: 18,
        borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
    },
    label: {
        padding: "4px 0",
    },
    selected: {}, // Để trống
}))((props) => <TreeItem {...props} TransitionComponent={TransitionComponent} />);

// Animation for tree items
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

export default React.memo(StyledTreeItem);
