import React, {PureComponent} from 'react';
import {Menu, Divider, Image, Segment, Statistic} from 'semantic-ui-react';
import IconMenu from './IconMenu';

class NavigationBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      activeItem: '',
      showSidebar: false
    };
  }

  toggleSidebar = () => this.setState({showSidebar: !this.state.showSidebar});

  render() {
    const {activeItem} = this.state;
    const stormwater = !!this.props.stormwater ? this.props.co2Reduced : {factor: '--', worth: '--'};
    const co2Reduced = !!this.props.co2Reduced ? this.props.co2Reduced : {factor: '--', worth: '--'};
    const biodiversity = !!this.props.biodiversity ? this.props.biodiversity : {factor: '--', worth: '--'};
    const energyConserved = !!this.props.energyConserved ? this.props.energyConserved : {factor: '--', worth: '--'};

    return (
      <div>
        <Menu stackable fluid widths={5}>
          <Menu.Item>
            <Image as='Button' circular color='green' size='tiny' src='../images/favicon.ico' onClick={this.toggleSidebar}/>
          </Menu.Item>

          <Menu.Item
            name='stormwater'
            fitted='vertically'
            active={activeItem === 'stormwater'}
          >
            <Segment.Group horizontal size='mini'>
              <Segment style={{display: 'flex', alignItems: 'center'}}>Stormwater Intercepted</Segment>
              <Segment>
                <Statistic horizontal size='mini' label='L/year' value={stormwater.factor}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={stormwater.worth}/>
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
                <Statistic horizontal size='mini' label='kg/year' value={co2Reduced.factor}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={co2Reduced.worth}/>
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
                <Statistic horizontal size='mini' label='' value={biodiversity.factor}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={biodiversity.worth}/>
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
                <Statistic horizontal size='mini' label='kWh/year' value={energyConserved.factor}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={energyConserved.worth}/>
              </Segment>
            </Segment.Group>
          </Menu.Item>
        </Menu>
        <IconMenu show={this.state.showSidebar}/>
      </div>
    );
  }
}

export default NavigationBar;