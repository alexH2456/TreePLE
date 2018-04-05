import React, {PureComponent} from 'react';
import {Menu, Divider, Segment} from 'semantic-ui-react';

class NavigationBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      activeItem: ''
    };
  }

  render() {
    const {activeItem} = this.state;
    const stormwater = !!this.props.stormwater ? this.props.co2Reduced : {factor: '--', worth: '--'};
    const co2Reduced = !!this.props.co2Reduced ? this.props.co2Reduced : {factor: '--', worth: '--'};
    const biodiversity = !!this.props.biodiversity ? this.props.biodiversity : {factor: '--', worth: '--'};
    const energyConserved = !!this.props.energyConserved ? this.props.energyConserved : {factor: '--', worth: '--'};

    return (
      <Menu stackable fluid widths={5}>
        <Menu.Item>
          ICON
        </Menu.Item>

        <Menu.Item
          name='stormwater'
          fitted='vertically'
          active={activeItem === 'stormwater'}
        >
          <Segment.Group horizontal size='mini'>
            <Segment style={{display: 'flex', alignItems: 'center'}}>Stormwater Intercepted</Segment>
            <Segment>
              {stormwater.factor} L/year
              <Divider fitted/>
              {stormwater.worth} $
            </Segment>
          </Segment.Group>
        </Menu.Item>

        <Menu.Item
          name='co2Reduced'
          fitted='vertically'
          active={activeItem === 'co2Reduced'}
        >
          <Segment.Group horizontal size='mini'>
            <Segment style={{display: 'flex', alignItems: 'center'}}>CO2 Reduced</Segment>
            <Segment>
              {co2Reduced.factor} kg/year
              <Divider fitted/>
              {co2Reduced.worth} $
            </Segment>
          </Segment.Group>
        </Menu.Item>

        <Menu.Item
          name='biodiversity'
          fitted='vertically'
          active={activeItem === 'biodiversity'}
        >
          <Segment.Group horizontal size='mini'>
            <Segment style={{display: 'flex', alignItems: 'center'}}>Biodiversity Index</Segment>
            <Segment>
              {biodiversity.factor}
              <Divider fitted/>
              {biodiversity.worth} $
            </Segment>
          </Segment.Group>

        </Menu.Item>

        <Menu.Item
          name='energyConserved'
          fitted='vertically'
          active={activeItem === 'energyConserved'}
        >
          <Segment.Group horizontal size='mini'>
            <Segment style={{display: 'flex', alignItems: 'center'}}>Energy Conserved</Segment>
            <Segment>
              {energyConserved.factor} kWh/year
              <Divider fitted/>
              {energyConserved.worth} $
            </Segment>
          </Segment.Group>
        </Menu.Item>
      </Menu>
    );
  }
}

export default NavigationBar;