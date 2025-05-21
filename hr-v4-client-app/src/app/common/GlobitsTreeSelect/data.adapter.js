export function adaptData(data, selectedValues) {
    return data.map(node => {
      if (selectedValues.includes(node.label)) {
        return {
          ...node,
          children: node.children ? adaptData(node.children, selectedValues) : [],
          checked: true
        };
      }
      return {
        ...node,
        checked: false,
        children: node.children ? adaptData(node.children, selectedValues) : []
      };
    });
  }