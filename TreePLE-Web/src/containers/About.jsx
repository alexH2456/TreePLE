import React, {PureComponent} from 'react';
import {Link} from 'react-router-dom';

class About extends PureComponent {
	render () {
		return (
			<div>
				About
				<Link to="/">
                    <button>Go Home</button>
                </Link>
			</div>
		);
	};
};

export default About;