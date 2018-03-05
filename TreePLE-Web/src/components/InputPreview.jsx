import React, {PureComponent} from 'react';

class InputPreview extends PureComponent {
  render () {
    return (
      <div>
        <input
          type="text"
          value={this.props.value}
          onChange={e => this.props.onChange(e.target.value)}
          />
      </div>
    );
  };
};

export default InputPreview;